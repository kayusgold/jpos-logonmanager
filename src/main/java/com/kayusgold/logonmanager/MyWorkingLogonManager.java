package com.kayusgold.logonmanager;

import java.util.Date;
import java.io.OutputStreamWriter;
import java.io.ByteArrayOutputStream;

import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;
import org.jpos.q2.QBeanSupport;
import org.jpos.core.Configuration;
import org.jpos.q2.iso.QMUX;
import org.jpos.space.Space;
import org.jpos.space.SpaceUtil;
import org.jpos.space.SpaceFactory;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.XMLPackager;
import org.jpos.iso.MUX;
import org.jpos.iso.ISODate;
import org.jpos.iso.ISOException;
import org.jpos.util.NameRegistrar;

@SuppressWarnings("unused unchecked")
public class MyWorkingLogonManager extends QBeanSupport {
    private Space sp;
    private Space psp;
    private long timeout;
    private long echoInterval;
    private long logonInterval;
    private long initialDelay;
    private ISOMsg logonMsg;
    private ISOMsg logoffMsg;
    private ISOMsg echoMsg;

    private static final String TRACE = "JC_TRACE";
    private static final String LOGON = "JC_LOGON.";
    private static final String ECHO  = "JC_ECHO.";

    public void initService () throws ConfigurationException {

        Configuration cfg = getConfiguration();
        sp = SpaceFactory.getSpace(cfg.get("space", ""));
        psp = SpaceFactory.getSpace(cfg.get("persistent-space", ""));
        timeout = cfg.getLong("timeout", 30000);
        echoInterval = cfg.getLong("echo-interval", 30000);
        logonInterval = cfg.getLong("logon-interval", 86400000L);
        initialDelay = cfg.getLong("initial-delay", 1000L);
        Element config = getPersist();
        logonMsg = getMsg("logon", config);
        logoffMsg = getMsg("logoff", config);
        echoMsg = getMsg("echo", config);
    }
    public void startService () {

        for (String mux : cfg.getAll("mux")) {
            new Thread(new Runner(mux), getName() + "-" + mux).start();
        }
    }

    public class Runner implements Runnable {
        String name;
        MUX mux;
        String readyKey;

        public Runner(String name) {
            this.name = name;

            try {
                mux = NameRegistrar.get("mux." + name);
                String[] readyIndicators = ((QMUX) mux).getReadyIndicatorNames();
                if (readyIndicators != null && readyIndicators.length > 0)
                    readyKey = readyIndicators[0];
                else
                    getLog().error("Ready indicator for MUX " + name + " not configured.");
            }
            catch (NameRegistrar.NotFoundException e) {
                getLog().warn(e);
            }
        }
        public void run () {
            while (running() && readyKey != null) {
                Object sessionId = sp.rd(readyKey, 60000);
                if (sessionId == null) {
                    getLog().info("Channel " + readyKey + " not ready");
                    continue;
                }

                try {
                    if (!sessionId.equals(sp.rdp(LOGON + readyKey))) {
                        doLogon(sessionId);
                        Thread.sleep(initialDelay);
                    }
                    else if (sp.rdp(ECHO + readyKey) == null) {
                        doEcho();
                    }
                }
                catch (Throwable t) {
                    getLog().warn(t);
                }
                ISOUtil.sleep(1000);
            }
            stopService();
        }

        public void stopService() {
            try {
                doLogoff();
            }
            catch (Throwable t) {
                getLog().warn(t);
            }
        }

        private void doLogon(Object sessionId) throws ISOException {
            ISOMsg resp = mux.request(createMsg("001", logonMsg), timeout);
            getLog().info("Logon response: " + resp.getString(39));
            if (resp != null && "00".equals(resp.getString(39))) {
                SpaceUtil.wipe(sp, LOGON + readyKey);
                sp.out(LOGON + readyKey, sessionId, logonInterval);
                getLog().info("Logon successful (session ID " + sessionId.toString() + ")");
            }
        }

        private void doLogoff () throws ISOException {
            SpaceUtil.wipe (sp, LOGON+readyKey);
            mux.request(createMsg("301", logoffMsg), 1000); // do not logoff
        }

        private void doEcho () throws ISOException {
            ISOMsg resp = mux.request(createMsg("301", echoMsg), timeout);
            if (resp != null) {
                sp.out(ECHO + readyKey, new Object(), echoInterval);
            }
        }

        private ISOMsg createMsg (String msgType, ISOMsg merge) throws ISOException {
            long traceNumber = SpaceUtil.nextLong(psp, TRACE) % 1000000;
            ISOMsg m = new ISOMsg ("0800");
            m.set(7, ISODate.getDateTime(new Date()));
            m.set(11, ISOUtil.zeropad(Long.toString(traceNumber), 6));
            m.set(12, ISODate.getTime(new Date()));
            m.set(13, ISODate.getDate(new Date()));
            m.set(24, "001");
            m.set(70, msgType);
            if (merge != null)
                m.merge (merge);

            return m;
        }
    }

    private ISOMsg getMsg(String name, Element config) throws ConfigurationException {
        ISOMsg m = new ISOMsg();
        Element e = config.getChild(name);

        if (e != null)
            e = e.getChild("isomsg");

        if (e != null) {
            try {
                XMLPackager p = new XMLPackager();
                p.setLogger(getLog().getLogger(), getLog().getRealm()
                        + "-config-" + name);
                m.setPackager(p);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                OutputStreamWriter writer = new OutputStreamWriter(os);
                XMLOutputter out = new XMLOutputter();
                out.output(e, writer);
                writer.close();
                m.unpack(os.toByteArray());
            }
            catch (Exception ex) {
                throw new ConfigurationException(ex);
            }
        }
        return m;
    }
}