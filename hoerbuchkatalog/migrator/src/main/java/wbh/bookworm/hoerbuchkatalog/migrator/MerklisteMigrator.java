/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.migrator;

import wbh.bookworm.hoerbuchkatalog.domain.bestellung.Merkliste;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;
import wbh.bookworm.hoerbuchkatalog.repository.bestellung.MerklisteRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public final class MerklisteMigrator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerklisteMigrator.class);

    private static String jdbcUrl = "jdbc:mysql://localhost:3306/bookworm";

    private static String jdbcUser;

    private static String jdbcSecret;

    @SuppressWarnings("java:S106")
    public static void main(String[] args) throws Exception {
        if (args.length != 4) {
            System.out.printf("usage: %s <JDBC url> <username> <password> <repository path>%n",
                    MerklisteMigrator.class.getSimpleName());
            System.exit(1);
        }
        final MerklisteRepository r = new MerklisteRepository(Path.of(args[3]));
        jdbcUrl = args[0];
        jdbcUser = args[1];
        jdbcSecret = args[2];
        ResultSet titelnummernResult = null;
        try (final Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcSecret);
             final PreparedStatement titelnummernStatement = connection.prepareStatement(
                     "SELECT BOOKS_TITELNUMMER FROM Wishlist_Book b WHERE WISHLIST_ID = ?");
             final PreparedStatement wunschlistenStatement = connection.prepareStatement(
                     "SELECT hoerernummer, id, version" +
                             " FROM Wishlist w" +
                             " WHERE (SELECT COUNT(*) FROM Wishlist_Book b WHERE b.WISHLIST_ID = w.id) > 0" +
                             " ORDER BY version ASC");
             final ResultSet wunschlistenResult = wunschlistenStatement.executeQuery()) {
            while (wunschlistenResult.next()) {
                final Hoerernummer hoerernummer = new Hoerernummer(wunschlistenResult.getString("hoerernummer"));
                final Merkliste merkliste = r.erstellen(hoerernummer);
                final String id = wunschlistenResult.getString("id");
                final int version = wunschlistenResult.getInt("version");
                titelnummernStatement.setString(1, id);
                titelnummernResult = titelnummernStatement.executeQuery();
                while (titelnummernResult.next()) {
                    final Titelnummer titelnummer = new Titelnummer(titelnummernResult.getString("BOOKS_TITELNUMMER"));
                    merkliste.hinzufuegen(titelnummer);
                }
                // Version anpassen = bisherige Version + 1
                while (merkliste.getVersion() < version) {
                    LOGGER.info("{} {} < {}", hoerernummer, merkliste.getVersion(), version);
                    merkliste.incVersion();
                }
                r.save(merkliste);
                titelnummernResult.close();
            }
        } finally {
            if (null != titelnummernResult) {
                titelnummernResult.close();
            }
        }
    }

}
