/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.infrastructure.blista.bestellung;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Vector;
import java.util.function.Consumer;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
class SftpClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SftpClient.class);

    private final DlsSftpConfig dlsSftpConfig;

    private Session session;

    private ChannelSftp channelSftp;

    @Autowired
    SftpClient(final DlsSftpConfig dlsSftpConfig) {
        this.dlsSftpConfig = dlsSftpConfig;
    }

    private void openSftpChannel() {
        final JSch jsch = new JSch();
        try {
            LOGGER.trace("Opening session with {}", dlsSftpConfig);
            session = jsch.getSession(dlsSftpConfig.getBibliothek(),
                    dlsSftpConfig.getHost(), dlsSftpConfig.getPort());
            session.setPassword(dlsSftpConfig.getBibkennwort());
            session.setConfig(getSessionConfig());
            session.connect();
            final Channel channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp) channel;
        } catch (JSchException e) {
            session = null;
            channelSftp = null;
            throw new SftpClientException(e);
        }
    }

    private Properties getSessionConfig() {
        final Properties config = new Properties();
        /* TODO Security */config.put("StrictHostKeyChecking", "no");
        return config;
    }

    private void disconnect() {
        if (null != session) {
            session.disconnect();
        }
    }

    <R> R with(final Consumer<ChannelSftpDelegate<R>> fun) {
        try {
            openSftpChannel();
            final ChannelSftpDelegate<R> channelSftpDelegate = new ChannelSftpDelegate<>();
            fun.accept(channelSftpDelegate);
            return channelSftpDelegate.result;
        } finally {
            disconnect();
        }
    }

    class ChannelSftpDelegate<R> {

        R result;

        void putOverwrite(final String src, final String dst) {
            try {
                final Path tempFile = Files.createTempFile("sftp", "");
                Files.write(tempFile, src.getBytes());
                channelSftp.put(tempFile.toAbsolutePath().toString(), dst, ChannelSftp.OVERWRITE);
                Files.delete(tempFile);
            } catch (IOException | SftpException e) {
                throw new SftpClientException(e);
            }
        }

        public void putOverwrite(final InputStream src, final String dst) {
            try {
                channelSftp.put(src, dst, ChannelSftp.OVERWRITE);
            } catch (SftpException e) {
                throw new SftpClientException(e);
            }
        }

        void cd(final String path) {
            try {
                channelSftp.cd(path);
            } catch (SftpException e) {
                throw new SftpClientException(e);
            }
        }

        @SuppressWarnings({"squid:S1149", "java:S1149"})
        public Vector ls(final String path) {
            try {
                return channelSftp.ls(path);
            } catch (SftpException e) {
                throw new SftpClientException(e);
            }
        }

        void ls(final String path, final ChannelSftp.LsEntrySelector selector) {
            try {
                channelSftp.ls(path, selector);
            } catch (SftpException e) {
                throw new SftpClientException(e);
            }
        }

        public String pwd() {
            try {
                return channelSftp.pwd();
            } catch (SftpException e) {
                throw new SftpClientException(e);
            }
        }

    }

}
