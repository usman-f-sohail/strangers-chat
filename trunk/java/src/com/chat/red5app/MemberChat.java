/**
 * File		: MemberChat.java
 * Date		: 13-Jan-2011 
 * Owner	: arul
 * Project	: MemberChat
 * Contact	: http://www.arulraj.net
 * Description : 
 * History	:
 */
package com.chat.red5app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.red5.io.utils.ObjectMap;
import org.red5.server.adapter.MultiThreadedApplicationAdapter;
import org.red5.server.api.IConnection;
import org.red5.server.api.IScope;
import org.red5.server.api.service.IPendingServiceCall;
import org.red5.server.api.service.IPendingServiceCallback;
import org.red5.server.api.service.ServiceUtils;
import org.red5.server.api.stream.IBroadcastStream;
import org.red5.server.api.stream.IStreamAwareScopeHandler;
import org.red5.server.api.stream.ISubscriberStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chat.model.ChatUser;
import com.chat.model.ChatUserConnection;
import com.chat.model.Conversation;
import com.chat.model.SearchCriteria;
import com.chat.model.Sex;
import com.chat.utils.ChatConstants;
import com.chat.utils.ChatUtility;

/**
 * @author arul
 *
 */
public class MemberChat extends MultiThreadedApplicationAdapter implements IPendingServiceCall, IStreamAwareScopeHandler{
	
	private static final Logger LOG = LoggerFactory.getLogger(MemberChat.class);
	private LinkedHashMap<String, ChatUser> usersMap = null;
	private LinkedHashMap<String, ChatUserConnection> usersConnectionMap = null;
	private LinkedHashMap<String, Conversation> conversationMap = null;
	
	/**
	 * Default constructor
	 */
	public MemberChat() {
		super();
		usersMap = new LinkedHashMap<String, ChatUser>();
		usersConnectionMap = new LinkedHashMap<String, ChatUserConnection>();
		conversationMap = new LinkedHashMap<String, Conversation>();
		setClientTTL(30);
		LOG.debug("memberchat constructor");
	}

	/* (non-Javadoc)
	 * @see org.red5.server.adapter.MultiThreadedApplicationAdapter#appStart(org.red5.server.api.IScope)
	 */
	@Override
	public boolean appStart(IScope scope) {
		boolean retVal =  super.appStart(scope);
		LOG.debug("MemberChat application started.");
		return retVal;
	}

	/* (non-Javadoc)
	 * @see org.red5.server.adapter.MultiThreadedApplicationAdapter#appStop(org.red5.server.api.IScope)
	 */
	@Override
	public void appStop(IScope scope) {
		LOG.debug("MemberChat application stopped.");
		super.appStop(scope);
	}

	/* (non-Javadoc)
	 * @see org.red5.server.adapter.MultiThreadedApplicationAdapter#connect(org.red5.server.api.IConnection, org.red5.server.api.IScope, java.lang.Object[])
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean connect(IConnection connection, IScope scope, Object[] params) {
		LOG.debug("Connecting to: " + scope.getPath() + "/" + scope.getName() +
	            " (" + scope.getContextPath() + ")");
        /*check this connction is Authenticated one*/
        if (isAuthenticateConnection(params, scope)) {
            LOG.debug("Connection from IP [" + connection.getRemoteAddress() + "] is successfully authenticated," +
            		" Connection ID [" + connection.getClient().getId() + "]");
        } else {
            /*if it is not a authenticated connection then close the connection*/
            LOG.debug("This is not an authenticated connection. Request from IP [" + connection.getRemoteAddress() + "]");
            rejectClient("This is not an authenticated connection.");
            return false;
        }		
        
        try {
        	ChatUser chatUser = null;
        	ChatUserConnection chatUserConnection = null;
            String connectionName = null;
            String userid = null;
            ObjectMap<String, Object> flexChatUser = null;
            
            if (params.length > 1) {
            	connectionName = (String) params[0];
            	flexChatUser = (ObjectMap<String, Object>) params[1];
            	userid = (String) flexChatUser.get("id");
            	LOG.debug("user from flex : "+flexChatUser.toString());
            	if(userid.equals(ChatConstants.GUEST)) {
            		LOG.debug("No ChatUser for the user [" + userid + "], Connection name [" + connectionName + "]");
            		userid = generateUserID();
            		chatUser = new ChatUser(userid);
            		chatUserConnection = new ChatUserConnection(userid);
            		usersMap.put(userid, chatUser);
            		usersConnectionMap.put(userid, chatUserConnection);
            	} else {
            		chatUser = usersMap.get(userid);
            		chatUserConnection = usersConnectionMap.get(userid);
            	}
            	/**
            	 * This will update user object from flex 
            	 */
            	chatUser.updateChatUser(flexChatUser);
            	LOG.debug(chatUser.toString());
            }

            if (chatUser != null && connectionName != null) {
            	if(connectionName.equals(ChatConstants.CONNECTIONS.get(0))) {
            		/*For public connection*/
            		chatUserConnection.setPublicId(connection.getClient().getId());
            		chatUserConnection.setPublicConnection(connection);
                	
                	List<HashMap<String, Object>> mapList = new ArrayList<HashMap<String,Object>>();
                	mapList.add(chatUser.genFlexChatUser());
                	ServiceUtils.invokeOnConnection(connection, "updateChatUser", new Object[]{chatUser});
                	
                    LOG.debug("Public Connection ID for the user [" + userid + "] is [" + chatUserConnection.getPublicId() + "]");            		
            	}else if (connectionName.equals(ChatConstants.CONNECTIONS.get(1))) {
            		/*For Video Connection*/
            		chatUserConnection.setVideoId(connection.getClient().getId());
            		chatUserConnection.setVideoConnection(connection);
                    LOG.debug("Video Connection ID for the user [" + userid + "] is [" + chatUserConnection.getVideoId() + "]");

                } else if (connectionName.equals(ChatConstants.CONNECTIONS.get(2))) {
                	/*For Audio Connection*/
                	chatUserConnection.setAudioId(connection.getClient().getId());
                	chatUserConnection.setAudioConnection(connection);
                    LOG.debug("Audio Connection ID for the user [" + userid + "] is [" + chatUserConnection.getAudioId() + "]");

                } 
            }
        } catch (Exception ex) {
            LOG.debug("Exception in appConnect... ", ex);
        } finally {
        }         
		return super.connect(connection, scope, params);
	}

	/* (non-Javadoc)
	 * @see org.red5.server.adapter.MultiThreadedApplicationAdapter#disconnect(org.red5.server.api.IConnection, org.red5.server.api.IScope)
	 */
	@Override
	public void disconnect(IConnection connection, IScope scope) {
		LOG.debug("Application dis-connect called from connection ID [" + connection.getClient().getId() + "]");
		try{
            String connId = connection.getClient().getId();
            ChatUser chatUser = getUserByPublicId(connId);
            ChatUserConnection connections = getChatConnectionByPublicId(connId);

            if (connections == null) {
            	connections = getChatConnectionByVideoId(connId);
            	chatUser = getUserByVideoId(connId);
                if (connections != null) {
                    if (connections.getVideoId() != null) {
                    	connections.setVideoId(null);
                        LOG.debug("Closing Video connection for user " + connections.getId() + ", Id [" + connId + "]");
                    }
                } else {
                	connections = getChatConnectionByAudioId(connId);
                	chatUser = getUserByAudioId(connId);
                    if (connections != null) {
                        if (connections.getAudioId() != null) {                        	
                        	connections.setAudioId(null);
                            LOG.debug("Closing Audio connection for user " + connections.getId() + ", Id [" + connId + "]");
                        }
                    } else {
                            LOG.warn("Could not find user for the requested Id [" + connId + "]");
                    }
                }
            } else {
            		String id = chatUser.getId();
                    LOG.debug("The user " + id + " is removed from Map, Id [" + connId + "]");
                    connections.setPublicId(null);
                    usersMap.remove(id);
                    conversationMap.remove(id);
                    usersConnectionMap.remove(id);
            }			
			
		}catch(Exception e){
			LOG.debug("Exception in appDisconnect....", e);
		}
		finally {
			super.disconnect(connection, scope);
		}
	}

	/* (non-Javadoc)
	 * @see org.red5.server.adapter.MultiThreadedApplicationAdapter#getSubscriberStream(org.red5.server.api.IScope, java.lang.String)
	 */
	@Override
	public ISubscriberStream getSubscriberStream(IScope scope, String name) {
		
		return super.getSubscriberStream(scope, name);
	}

	/* (non-Javadoc)
	 * @see org.red5.server.adapter.MultiThreadedApplicationAdapter#hasBroadcastStream(org.red5.server.api.IScope, java.lang.String)
	 */
	@Override
	public boolean hasBroadcastStream(IScope scope, String name) {
		
		return super.hasBroadcastStream(scope, name);
	}

	/* (non-Javadoc)
	 * @see org.red5.server.adapter.MultiThreadedApplicationAdapter#streamBroadcastClose(org.red5.server.api.stream.IBroadcastStream)
	 */
	@Override
	public void streamBroadcastClose(IBroadcastStream stream) {
		String name = stream.getPublishedName();
		LOG.debug("Stream broadcast close for stream name : " + name);		
		super.streamBroadcastClose(stream);
	}

	/* (non-Javadoc)
	 * @see org.red5.server.adapter.MultiThreadedApplicationAdapter#streamBroadcastStart(org.red5.server.api.stream.IBroadcastStream)
	 */
	@Override
	public void streamBroadcastStart(IBroadcastStream stream) {
		String name = stream.getPublishedName();
		LOG.debug("Stream broadcast start for stream name : " + name);			
		super.streamBroadcastStart(stream);
	}

	/* (non-Javadoc)
	 * @see org.red5.server.adapter.MultiThreadedApplicationAdapter#streamPublishStart(org.red5.server.api.stream.IBroadcastStream)
	 */
	@Override
	public void streamPublishStart(IBroadcastStream stream) {
		String name = stream.getPublishedName();
		LOG.debug("Stream publish start for stream name : " + name);		
		super.streamPublishStart(stream);
	}

	/* (non-Javadoc)
	 * @see org.red5.server.adapter.MultiThreadedApplicationAdapter#streamRecordStart(org.red5.server.api.stream.IBroadcastStream)
	 */
	@Override
	public void streamRecordStart(IBroadcastStream stream) {
		String name = stream.getPublishedName();
		LOG.debug("Stream Record start for stream name : " + name);		
		super.streamRecordStart(stream);
	}

	/* (non-Javadoc)
	 * @see org.red5.server.adapter.MultiThreadedApplicationAdapter#streamSubscriberClose(org.red5.server.api.stream.ISubscriberStream)
	 */
	@Override
	public void streamSubscriberClose(ISubscriberStream stream) {
		LOG.info("Stream Subscriber Close stream name: " + stream.getName());
		super.streamSubscriberClose(stream);
	}

	/* (non-Javadoc)
	 * @see org.red5.server.adapter.MultiThreadedApplicationAdapter#streamSubscriberStart(org.red5.server.api.stream.ISubscriberStream)
	 */
	@Override
	public void streamSubscriberStart(ISubscriberStream stream) {
		LOG.info("Stream Subscriber Start stream name: " + stream.getName());
		super.streamSubscriberStart(stream);
	}

	/* (non-Javadoc)
	 * @see org.red5.server.api.service.IServiceCall#getArguments()
	 */
	@Override
	public Object[] getArguments() {
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.red5.server.api.service.IServiceCall#getException()
	 */
	@Override
	public Exception getException() {
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.red5.server.api.service.IServiceCall#getServiceMethodName()
	 */
	@Override
	public String getServiceMethodName() {
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.red5.server.api.service.IServiceCall#getServiceName()
	 */
	@Override
	public String getServiceName() {
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.red5.server.api.service.IServiceCall#getStatus()
	 */
	@Override
	public byte getStatus() {
		
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.red5.server.api.service.IServiceCall#isSuccess()
	 */
	@Override
	public boolean isSuccess() {
		
		return false;
	}

	/* (non-Javadoc)
	 * @see org.red5.server.api.service.IServiceCall#setException(java.lang.Exception)
	 */
	@Override
	public void setException(Exception arg0) {
		
		
	}

	/* (non-Javadoc)
	 * @see org.red5.server.api.service.IServiceCall#setStatus(byte)
	 */
	@Override
	public void setStatus(byte arg0) {
		
		
	}

	/* (non-Javadoc)
	 * @see org.red5.server.api.service.IPendingServiceCall#getCallbacks()
	 */
	@Override
	public Set<IPendingServiceCallback> getCallbacks() {
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.red5.server.api.service.IPendingServiceCall#getResult()
	 */
	@Override
	public Object getResult() {
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.red5.server.api.service.IPendingServiceCall#registerCallback(org.red5.server.api.service.IPendingServiceCallback)
	 */
	@Override
	public void registerCallback(IPendingServiceCallback arg0) {
		
		
	}

	/* (non-Javadoc)
	 * @see org.red5.server.api.service.IPendingServiceCall#setResult(java.lang.Object)
	 */
	@Override
	public void setResult(Object arg0) {
		
		
	}

	/* (non-Javadoc)
	 * @see org.red5.server.api.service.IPendingServiceCall#unregisterCallback(org.red5.server.api.service.IPendingServiceCallback)
	 */
	@Override
	public void unregisterCallback(IPendingServiceCallback arg0) {
		
		
	}
	
	
	/**
	 * USER DEFINED FUNCTION
	 */
	
	/**
	 * This function for is that RTMP connection is created from authenticate client.
	 * 
	 * @author Arul
	 * @param params
	 */
	 private boolean isAuthenticateConnection(Object[] params, IScope scope) {
        boolean isAuthenticate = true;
        /*Here the list of rtmp connections name*/
        List<String> connectionList = ChatConstants.CONNECTIONS;

        if (params != null) {
            if (params.length == 2) {
                String connectionName = (String) params[0];
                connectionName = connectionName.trim();

                if (connectionList.contains(connectionName)) {
                	if(ChatConstants.ROOMS.contains(scope.getName())) {
                		isAuthenticate = true;
                	} else {
                		/*The scope is not the specified one*/
                		isAuthenticate = false;
                	}
                
                } else {
                    /*If the connection params purpose is not the specified - kill that connection*/
                    isAuthenticate = false;
                }

            } else {
                /*If the connection params length wrong- kill that connection*/
                isAuthenticate = false;
            }

        } else {
            /*If the connection without the params - kill that connection*/
            isAuthenticate = false;
        }
        return isAuthenticate;
	 }
	 
	 /**
	  * 
	  * @return
	  */
	 private String generateUserID() {
		 String username = null;		 
		 username = ChatUtility.generateString(ChatConstants.USER_ID_LENGTH);
		 while(usersMap.containsKey(username)){
			 username = ChatUtility.generateString(ChatConstants.USER_ID_LENGTH);
		 }
		 return username;
	 }
	 
 	/**
 	 * 
 	 * @param id
 	 * @return
 	 */
 	private ChatUser getUserByPublicId(String id) {
        try {
            Collection<ChatUserConnection> cl = usersConnectionMap.values();
            Iterator<ChatUserConnection> itr = cl.iterator();
            while (itr.hasNext()) {
            	ChatUserConnection connection = (ChatUserConnection) itr.next();
            	String chatId = connection.getPublicId();
                if (connection != null && chatId != null && chatId.equals(id)) {
                    return usersMap.get(connection.getId());
                }
            }
        } catch (Exception e) {
            LOG.debug("Exception in getUserByChatId() " , e);
        } finally {
        }
        return null;
    }
 	
	/**
	 * 
	 * @param id
	 * @return
	 */
	private ChatUser getUserByVideoId(String id) {
        try {
            Collection<ChatUserConnection> cl = usersConnectionMap.values();
            Iterator<ChatUserConnection> itr = cl.iterator();
            while (itr.hasNext()) {
            	ChatUserConnection connection = (ChatUserConnection) itr.next();
            	String videoId = connection.getVideoId();
                if (connection != null && videoId != null && videoId.equals(id)) {
                    return usersMap.get(connection.getId());
                }
            }
        } catch (Exception e) {
        	LOG.debug("Exception in getUserByVideoId() " , e);
        } finally {
        }
        return null;
    }
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	private ChatUser getUserByAudioId(String id) {
        try {
            Collection<ChatUserConnection> cl = usersConnectionMap.values();
            Iterator<ChatUserConnection> itr = cl.iterator();
            while (itr.hasNext()) {
            	ChatUserConnection connection = (ChatUserConnection) itr.next();
            	String audioId = connection.getAudioId();
                if (connection != null && audioId != null && audioId.equals(id)) {
                    return usersMap.get(connection.getId());
                }
            }
        } catch (Exception e) {
        	LOG.debug("Exception in getUserByAudioId() " , e);
        } finally {
        }
        return null;
    }
	
	/**
 	 * 
 	 * @param id
 	 * @return
 	 */
 	private ChatUserConnection getChatConnectionByPublicId(String id) {
        try {
            Collection<ChatUserConnection> cl = usersConnectionMap.values();
            Iterator<ChatUserConnection> itr = cl.iterator();
            while (itr.hasNext()) {
            	ChatUserConnection connection = (ChatUserConnection) itr.next();
            	String chatId = connection.getPublicId();
                if (connection != null && chatId != null && chatId.equals(id)) {
                    return connection;
                }
            }
        } catch (Exception e) {
            LOG.debug("Exception in getUserByChatId() " , e);
        } finally {
        }
        return null;
    }
 	
	/**
	 * 
	 * @param id
	 * @return
	 */
	private ChatUserConnection getChatConnectionByVideoId(String id) {
        try {
            Collection<ChatUserConnection> cl = usersConnectionMap.values();
            Iterator<ChatUserConnection> itr = cl.iterator();
            while (itr.hasNext()) {
            	ChatUserConnection connection = (ChatUserConnection) itr.next();
            	String videoId = connection.getVideoId();
                if (connection != null && videoId != null && videoId.equals(id)) {
                    return connection;
                }
            }
        } catch (Exception e) {
        	LOG.debug("Exception in getUserByVideoId() " , e);
        } finally {
        }
        return null;
    }
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	private ChatUserConnection getChatConnectionByAudioId(String id) {
        try {
            Collection<ChatUserConnection> cl = usersConnectionMap.values();
            Iterator<ChatUserConnection> itr = cl.iterator();
            while (itr.hasNext()) {
            	ChatUserConnection connection = (ChatUserConnection) itr.next();
            	String audioId = connection.getAudioId();
                if (connection != null && audioId != null && audioId.equals(id)) {
                    return connection;
                }
            }
        } catch (Exception e) {
        	LOG.debug("Exception in getUserByAudioId() " , e);
        } finally {
        }
        return null;
    }
	
	
	/**
	 * Remote call functions
	 */
	/**
	 * update chat user
	 */
	public void updateChatUser(ObjectMap<String, Object> flexChatUser) {
		try {
			 String userid = (String) flexChatUser.get("id");
			 ChatUser chatUser = usersMap.get(userid);
			 chatUser.updateChatUser(flexChatUser);
			 
			 /*LOG.debug("updateChatUser : "+chatUser.toString());
			 usersMap.put(userid, chatUser);		
			 LOG.debug("usermap.get() : "+usersMap.get(userid).toString());*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * scan and get the random partner
	 */
	public void scanRandomPartner(ObjectMap<String, Object> flexCriteria, ObjectMap<String, Object> flexChatUser) {
		try {
			LOG.debug("search criteria "+flexCriteria.toString());
			LOG.debug("Chat user "+ flexChatUser.toString());
			updateChatUser(flexChatUser);
			String userid = (String) flexChatUser.get("id");
			Conversation yourConversation = null;
			Conversation partnerConversation = null;
			if(conversationMap.get(userid) != null) {
				yourConversation = conversationMap.get(userid);
			} else {
				yourConversation = new Conversation(userid);
			}
			SearchCriteria scanCriteria = new SearchCriteria();
			List<String> scanResult = new ArrayList<String>();
			LOG.debug(" before "+scanCriteria.toString());
			scanCriteria.updateCriteria(flexCriteria);
			LOG.debug("after "+scanCriteria.toString());
			Iterator<String> itr = usersMap.keySet().iterator();
			while(itr.hasNext()) {
				String partnerId = itr.next();
				ChatUser partnerUser = usersMap.get(partnerId);
				if(partnerUser.isAllowScan()) {
					if(partnerUser.isRegisteredUser()) {
						/*If the user is registered user - TO DO from db*/
						// Get the play permission for video and audio from DB
						
						
					} else {
						/*By default the video/audio play permission is enabled for all*/
						yourConversation.setPlayAudioPermission(true);
						yourConversation.setPlayVideoPermission(true);
						
						if(partnerUser.isVideo() && !partnerUser.getId().equals(yourConversation.getId())) {
							LOG.debug("Partner "+partnerUser.toString());
							/*the partner user should publish his video for chating*/
							if(scanCriteria.getSex().equals(Sex.ALL.getValue()) || 
									scanCriteria.getSex().equals(partnerUser.getSex())) {
								
								if(scanCriteria.getPlace().equals(ChatConstants.ALL) || 
										scanCriteria.getPlace().equals(partnerUser.getPlace())) {
									
									if(partnerUser.getAge() >= scanCriteria.getStartAge() && partnerUser.getAge() <= scanCriteria.getEndAge() ) {
										scanResult.add(partnerUser.getId());
									}
								}
							}
						}
					}
				}
			}
			
			LOG.debug("scanResult "+scanResult.toString());
			
			if(!scanResult.isEmpty()) {
				 Collections.shuffle(scanResult);			 
				 int randomIndex = new Random().nextInt(scanResult.size());
				 yourConversation.setPartnerUser(usersMap.get(scanResult.get(randomIndex)));
				 conversationMap.put(userid, yourConversation);
			}
			LOG.debug("conversion "+yourConversation.toString());
			LOG.debug("chat connection "+usersConnectionMap.get(userid).toString() + " usersConnectionMap.get(userid).getPublicConnection() " +usersConnectionMap.get(userid).getPublicConnection().toString());
			
			ServiceUtils.invokeOnConnection(usersConnectionMap.get(userid).getPublicConnection(), "updateConversation", new Object[]{yourConversation.getFlexConversation()});
			
			/*update conversation for partner*/
			if(yourConversation.getPartnerUser() != null) {
				ChatUser partnerUser = yourConversation.getPartnerUser();
				ChatUser yourUser = usersMap.get(userid);
				if(conversationMap.get(partnerUser.getId()) != null) {
					partnerConversation = conversationMap.get(partnerUser.getId());
				} else {
					partnerConversation = new Conversation(partnerUser.getId());
					conversationMap.put(partnerUser.getId(), partnerConversation);
				}
				if(yourUser.isRegisteredUser()) {
					
				} else {
					partnerConversation.setPlayAudioPermission(true);
					partnerConversation.setPlayVideoPermission(true);
				}
				partnerConversation.setPartnerUser(yourUser);
				ServiceUtils.invokeOnConnection(usersConnectionMap.get(partnerUser.getId()).getPublicConnection(), "updateConversation", new Object[]{partnerConversation.getFlexConversation()});
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * when stop button clicked
	 * @param flexChatUser
	 */
	public void stopRandomScan(ObjectMap<String, Object> flexChatUser) {
		LOG.debug("stop random scan Chat user "+ flexChatUser.toString());
		updateChatUser(flexChatUser);
		String userid = (String) flexChatUser.get("id");
		String partnerId = conversationMap.get(userid).getPartnerUser().getId();
		Conversation emptyConversation = new Conversation(ChatConstants.GUEST);
		
		conversationMap.remove(userid);
		ServiceUtils.invokeOnConnection(usersConnectionMap.get(userid).getPublicConnection(), "updateConversation", new Object[]{emptyConversation.getFlexConversation()});
		if(conversationMap.get(partnerId).getPartnerUser().getId().equals(userid)) {
			conversationMap.remove(partnerId);
			ServiceUtils.invokeOnConnection(usersConnectionMap.get(partnerId).getPublicConnection(), "updateConversation", new Object[]{emptyConversation.getFlexConversation()});
		}
	}
	
	/**
	 * publish the chat message to the particular client
	 * @param flexConversation
	 * @param message
	 * @return
	 */
	public void broadcastChatMessage(String userid, String message) {
		 try{
			 /*String userid = (String) flexConversation.get("id");
			 String partnerId = ChatUtility.getChatUser((ObjectMap<String,Object>)flexConversation.get("partnerUser")).getId();*/
			 String partnerId = conversationMap.get(userid).getPartnerUser().getId();
			 Conversation conversation = conversationMap.get(partnerId);
			 if(conversation.getPartnerUser().getId().equals(userid)) {
				 ChatUserConnection connections = usersConnectionMap.get(partnerId);
				 if(connections != null) {
					 IConnection partnerConnection = connections.getPublicConnection();
					 ServiceUtils.invokeOnConnection(partnerConnection, "receiveMessage", new Object[]{message});
				 }
			 }
		 } catch(Exception e) {
			 LOG.debug("Exception in broadcast chat message ",e);
		 } finally {
			 
		 }
	 }
}
