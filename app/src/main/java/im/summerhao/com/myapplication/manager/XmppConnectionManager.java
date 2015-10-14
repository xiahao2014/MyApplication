package im.summerhao.com.myapplication.manager;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;

import im.summerhao.com.myapplication.mode.LoginConfig;

/**
 * XMPP 管理类
 * Created by lenovo on 2015/10/13.
 */
public class XmppConnectionManager {

    private static XMPPConnection connection;
    private static ConnectionConfiguration connectionConfig;
    private static XmppConnectionManager xmppConnectionManager;

    private XmppConnectionManager() {
    }

    public static XmppConnectionManager getInstance() {
        if (xmppConnectionManager == null) {
            xmppConnectionManager = new XmppConnectionManager();
        }
        return xmppConnectionManager;
    }

    public XMPPConnection init(LoginConfig loginConfig) {
        Connection.DEBUG_ENABLED = false;
        ProviderManager pm = ProviderManager.getInstance();
        configure(pm);

        connectionConfig = new ConnectionConfiguration(
                loginConfig.getXmppHost(), loginConfig.getXmppPort(),
                loginConfig.getXmppServiceName());
        // 不使用SASL验证，设置为false
        connectionConfig.setSASLAuthenticationEnabled(false);
        //安全模式
        connectionConfig.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled);
        // 允许自动连接
        connectionConfig.setReconnectionAllowed(false);
        // 允许登陆成功后更新在线状态
        connectionConfig.setSendPresence(true);
        // 收到好友邀请后manual表示需要经过同意,accept_all表示不经同意自动为好友
        Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.manual);
        connection = new XMPPConnection(connectionConfig);
        return connection;
    }

    public XMPPConnection getConnection() {
        if (connection == null) {
            throw new RuntimeException("请先初始化XMPPConnection连接");
        }
        return connection;
    }

    public void disconnect() {
        if (connection != null) {
            connection.disconnect();
        }
    }


    public void configure(ProviderManager pm) {
        pm.addIQProvider("query", "jabber:iq:private",
                new PrivateDataManager.PrivateDataIQProvider());

        try {
            pm.addIQProvider("query", "jabber:iq:time",
                    Class.forName("org.jivesoftware.smackx.packet.Time"));
        } catch (ClassNotFoundException e) {
        }

        pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im",
                new XHTMLExtensionProvider());
        pm.addExtensionProvider("x", "jabber:x:roster",
                new RosterExchangeProvider());
        pm.addExtensionProvider("x", "jabber:x:event",
                new MessageEventProvider());
        pm.addExtensionProvider("active",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());
        pm.addExtensionProvider("composing",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());
        pm.addExtensionProvider("paused",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());
        pm.addExtensionProvider("inactive",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());
        pm.addExtensionProvider("gone",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());

        pm.addIQProvider("si", "http://jabber.org/protocol/si",
                new StreamInitiationProvider());

        pm.addExtensionProvider("x", "jabber:x:conference",
                new GroupChatInvitation.Provider());

        pm.addIQProvider("query", "http://jabber.org/protocol/disco#items",
                new DiscoverItemsProvider());

        pm.addIQProvider("query", "http://jabber.org/protocol/disco#info",
                new DiscoverInfoProvider());

        pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());

        pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user",
                new MUCUserProvider());

        pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin",
                new MUCAdminProvider());

        pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner",
                new MUCOwnerProvider());

        pm.addExtensionProvider("x", "jabber:x:delay",
                new DelayInformationProvider());

        try {
            pm.addIQProvider("query", "jabber:iq:version",
                    Class.forName("org.jivesoftware.smackx.packet.Version"));
        } catch (ClassNotFoundException e) {
        }

        pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());

        pm.addIQProvider("offline", "http://jabber.org/protocol/offline",
                new OfflineMessageRequest.Provider());

        pm.addExtensionProvider("offline",
                "http://jabber.org/protocol/offline",
                new OfflineMessageInfo.Provider());

        pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());

        pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());

        pm.addIQProvider("sharedgroup",
                "http://www.jivesoftware.org/protocol/sharedgroup",
                new SharedGroupsInfo.Provider());

        pm.addExtensionProvider("addresses",
                "http://jabber.org/protocol/address",
                new MultipleAddressesProvider());

    }
}
