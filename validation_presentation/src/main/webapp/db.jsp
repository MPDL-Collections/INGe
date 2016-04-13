<%@page import="de.mpg.escidoc.services.framework.PropertyReader"%>
<%@page import="org.apache.commons.codec.binary.Base64"%>
<%@page import="java.sql.SQLException"%>
<%@page import="java.sql.ResultSetMetaData"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="javax.sql.DataSource"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="javax.naming.Context"%>
<%@page import="java.sql.Statement"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%
/*
*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.org/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/
%>
<%!
	private boolean testLogin(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String auth = request.getHeader("authorization");
		if (auth == null)
		{
			response.addHeader("WWW-Authenticate", "Basic realm=\"Validation Service\"");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return false;
		}
		else
		{
			auth = auth.substring(6);
			String cred = new String(Base64.decodeBase64(auth.getBytes()));
			if (cred.contains(":"))
			{
	
				String[] userPass = cred.split(":");
				String userName = PropertyReader.getProperty("framework.admin.username");
				String password = PropertyReader.getProperty("framework.admin.password");
	
				if (!userPass[0].equals(userName) || !userPass[1].equals(password))
				{
					response.sendError(HttpServletResponse.SC_FORBIDDEN);
					return false;
				}
				else
				{
					return true;
				}
			}
			else
			{
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				return false;
			}
		}
	}
%>
<%
	if (testLogin(request, response))
	{
%>
<html>
	<head>
		<title>eSciDoc Validation Service</title>
	</head>
	<body bgcolor="white">
		<h1>
			eSciDoc Validation Service
		</h1>
		<p>
			This is a makeshift to administer the validation cache database until a proper frontend is available.
		</p>
		<form method="post" action="db.jsp">
			Query:
			<br/>
			<textarea rows="10" cols="100" name="sql_query"><%= (request.getParameter("sql_query") == null ? "" : request.getParameter("sql_query")) %></textarea>
			<br/>
			<input type="submit"/>
		</form>
		<form method="post" action="db.jsp">	
			Update:
			<br/>
			<textarea rows="10" cols="100" name="sql_update"><%= (request.getParameter("sql_update") == null ? "" : request.getParameter("sql_update")) %></textarea>
			<br/>
			<input type="submit"/>
		</form>
		<%
			if (request.getParameter("sql_query") != null || request.getParameter("sql_update") != null )
			{ %>
			    Result [<% out.println( new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(new Date())); %>]
			    <br/>
				<% Context ctx = new InitialContext();
	            DataSource dataSource = (DataSource) ctx.lookup("java:jboss/datasources/Validation");
	            Statement pstmt = dataSource.getConnection().createStatement();
	            try
	            {
		            
	            	if(request.getParameter("sql_query") != null)
	            	{
	            		 ResultSet rs = pstmt.executeQuery(request.getParameter("sql_query"));
	            		
	            		 ResultSetMetaData resultSetMetaData = rs.getMetaData();
	     	            
	 		            %><table cellspacing="1" bgcolor="#EEEEEE"><thead><%
	 					for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++)
	 					{
	 					    %><th><%= resultSetMetaData.getColumnName(i) %></th><%
	 					}
	 		            %></thead><%
	 		            while (rs.next())
	 		            {
	 		                %><tr><%
	 		                for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++)
	 						{
	 						    %><td valign="top"><%= (rs.getObject(i) == null ? "---" : rs.getObject(i).toString().replace("&", "&amp;").replace("<", "&lt;").replace("\n", "<br/>")) %></td><%
	 						}
	 		                %></tr><%
	 		            }
	 		            %></table><%
	            		
	            		
	            	}
	            	else if (request.getParameter("sql_update") != null)
	            	{
	            		int count = pstmt.executeUpdate(request.getParameter("sql_update"));
	            		out.println("Successfully updated " + count + " rows.");
	            	}
	            	
		            
		            
		            
		           
	            }
	            catch (SQLException sqle)
	            {
	                if ("General errorresult set is closed".equals(sqle.getMessage()))
	                {
	                    out.println("The query returned no result.");
	                }
	                else
	                {
	                    out.println("Error: " + sqle.getMessage());
	                }
	            }
	            finally
	            {
	            	dataSource.getConnection().close();
	            }
			}
		%>
	</body>
</html>
<%
	}
%>