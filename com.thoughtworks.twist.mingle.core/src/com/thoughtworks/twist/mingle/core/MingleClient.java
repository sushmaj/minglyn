/*******************************************************************************
 * Copyright (c) 2008 ThoughtWorks, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ThoughtWorks, Inc. - initial API and implementation
 *******************************************************************************/
package com.thoughtworks.twist.mingle.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.PartBase;
import org.apache.commons.httpclient.methods.multipart.PartSource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentSource;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * @author Ketan Padegaonkar
 * @version $Id$
 */
public class MingleClient {

	private HttpClient						httpClient;
	private final URL						projectUrl;
	private final String					userName;
	private final String					password;
	private final MingleTaskAttributeMapper	taskAttributeMapper;

	public MingleClient(String projectUrl, String userName, String password, MingleTaskAttributeMapper taskAttributeMapper) {
		try {
			this.projectUrl = new URL(projectUrl);
			this.userName = userName;
			this.password = password;
			this.taskAttributeMapper = taskAttributeMapper;
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public MingleClient(TaskRepository taskRepository, MingleTaskAttributeMapper taskAttributeMapper) {
		try {
			this.projectUrl = new URL(taskRepository.getUrl());
			this.userName = taskRepository.getCredentials(AuthenticationType.HTTP).getUserName();
			this.password = taskRepository.getCredentials(AuthenticationType.HTTP).getPassword();
			this.taskAttributeMapper = taskAttributeMapper;
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean validate() {
		HttpMethod method = getMethod(loginUrl());
		try {
			int httpStatus = executeMethod(method);
			switch (httpStatus) {
			case HttpStatus.SC_OK:
				return true;
			case HttpStatus.SC_UNAUTHORIZED:
				throw new MingleAuthenticationException();
			}
		} finally {
			if (method != null)
				method.releaseConnection();
		}
		return false;
	}

	protected int executeMethod(HttpMethod method) {
		try {
			return getClient().executeMethod(method);
		} catch (HttpException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected HttpClient getClient() {
		if (this.httpClient == null) {
			this.httpClient = new HttpClient();
			this.httpClient.getParams().setAuthenticationPreemptive(true);
			Credentials defaultcreds = new UsernamePasswordCredentials(userName, password);
			AuthScope authScope = new AuthScope(this.projectUrl.getHost(), this.projectUrl.getPort(), AuthScope.ANY_REALM);
			this.httpClient.getState().setCredentials(authScope, defaultcreds);
		}
		return this.httpClient;
	}

	private String loginUrl() {
		String url = projectUrl.toString();
		return url.substring(0, url.indexOf("/projects"));
	}

	protected Reader getResponse(HttpMethod method) {
		try {
			return new InputStreamReader(method.getResponseBodyAsStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected HttpMethod getMethod(String url) {
		return new GetMethod(url);
	}

	public TaskDataList getAllTaskData() {
		return getAllTaskData(queryUrl(""));
	}

	public TaskDataList getAllTaskData(String queryUrl) {
		HttpMethod method = getMethod(queryUrl(queryUrl));
		try {
			switch (executeMethod(method)) {
			case HttpStatus.SC_OK:
				return new TaskDataList((List) parse(getResponse(method)));
			case HttpStatus.SC_UNAUTHORIZED:
				throw new MingleAuthenticationException("Could not authenticate user. Check username and password.");
			}
			return new TaskDataList();
		} finally {
			if (method != null)
				method.releaseConnection();
		}
	}

	public TaskData getTaskData(String taskId) {
		HttpMethod method = getMethod(cardUrl(taskId));
		try {
			switch (executeMethod(method)) {
			case HttpStatus.SC_OK:
				return (TaskData) parse(getResponse(method));
			case HttpStatus.SC_UNAUTHORIZED:
				throw new MingleAuthenticationException("Could not authenticate user. Check username and password.");
			default:
				throw new RuntimeException("Got an http response that I do not know how to handle");
			}
		} finally {
			if (method != null)
				method.releaseConnection();
		}
	}

	private Object parse(Reader inputStreamReader) {
		try {
			XStream stream = new XStream(new DomDriver());
			stream.registerConverter(new MingleTaskConverter(projectUrl, taskAttributeMapper));
			stream.alias("cards", ArrayList.class);
			stream.alias("card", TaskData.class);
			return stream.fromXML(inputStreamReader);
		} catch (Exception e) {
			throw new CouldNotParseTasksException(e);
		}
	}

	public String queryUrl(String queryString) {
		return projectUrl + "/cards.xml?" + queryString;
	}

	public String cardUrl(String taskId) {
		return projectUrl + "/cards/" + taskId + ".xml";
	}

	public String attachmentUrl(String taskId) {
		return projectUrl + "/cards/update/" + cardId(taskId);
	}

	public String commentUrl(String taskId) {
		return projectUrl + "/cards/add_comment?card_id=" + cardId(taskId);
	}

	private String cardId(String taskId) {
		// mingle has a notion of card id and there's an internal card id that it maintains for a given card id.
		// comments use the internal card id, and therefore this mess.
		TaskData taskData = getTaskData(taskId);
		TaskAttribute root = taskData.getRoot();
		TaskAttribute mappedAttribute = root.getMappedAttribute("task.id");
		String value = taskData.getAttributeMapper().getValue(mappedAttribute);
		return value;
	}

	public void attachFile(String taskId, String comment, String description, AbstractTaskAttachmentSource source, String filename,
			IProgressMonitor monitor) {

		PostMethod post = new PostMethod(attachmentUrl(taskId));

		List<PartBase> parts = new ArrayList<PartBase>();

		String fileName = source.getName();
		parts.add(new FilePart("attachments[0]", new AttachmentPartSource(source)));
		
		post.setRequestEntity(new MultipartRequestEntity(parts.toArray(new Part[1]), post.getParams()));

		
		try {
			int executeMethod = getClient().executeMethod(post);
			String responseBodyAsString = post.getResponseBodyAsString();
			System.out.println();
		} catch (HttpException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			post.releaseConnection();
		}
	}

	public void attachComment(String taskId, String comment, String description, InputStream createInputStream, String filename,
			IProgressMonitor monitor) {

		PostMethod post = new PostMethod(commentUrl(taskId));

		NameValuePair[] data = { new NameValuePair("comment", comment), };
		post.setRequestBody(data);

		try {
			getClient().executeMethod(post);
		} catch (HttpException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			post.releaseConnection();
		}
	}

	public static class AttachmentPartSource implements PartSource {

		private final AbstractTaskAttachmentSource	attachment;

		private final String						filename;

		public AttachmentPartSource(AbstractTaskAttachmentSource attachment) {
			this.attachment = attachment;
			this.filename = attachment.getName();
		}

		public InputStream createInputStream() throws IOException {
			try {
				return attachment.createInputStream(null);
			} catch (CoreException e) {
				throw new IOException("Failed to create source stream");
			}
		}

		public String getFileName() {
			return filename;
		}

		public long getLength() {
			return attachment.getLength();
		}

	}
}
