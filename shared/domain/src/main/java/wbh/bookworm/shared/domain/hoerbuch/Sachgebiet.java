/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.shared.domain.hoerbuch;

/**
 * Value Object
 */
public enum Sachgebiet {

    A("Klassiker der Weltliteratur"),
    B("Erzählungen oder Novellen, Kurzgeschichten, Märchen und Sagen"),
    C("Lyrik und Anthologien"),
    D("Literatur der Moderne und Problemliteratur"),
    E("Unterhaltungsliteratur – Schicksalsromane – Liebesromane"),
    F("Historische Romane"),
    G("Kriminal-, Agenten-, Abenteuerromane, Kriegserlebnisse, Western"),
    H("Humor und Satire"),
    I("Science Fiction - Phantastische Literatur"),
    J("Biographien – Erinnerungen – Tagebücher – Briefe"),
    K("Literatur – Musik – Kunst"),
    L("Philosophie – Psychologie – Religion – Religiöse Literatur"),
    M("Städte – Länder – Völker – Reisen – Expeditionen"),
    N("Geschichte – Zeitgeschichte – Kulturgeschichte – Archäologie"),
    O("Politik - Gesellschaft - Pädagogik"),
    P("Recht - Wirtschaft"),
    Q("Naturwissenschaft - Medizin - Technik"),
    R("Tiergeschichten - Tierverhalten"),
    S("Kinder- und Jugendbücher"),
    T("Hobbys- Praktische Bücher – Ratgeber – Weiterbildung"),
    U("Fremdsprachige Bücher"),
    V("Blindenwesen"),
    W("Hörspiele – Dramen"),
    X("Stimme des Autors"),
    Z("Bücher in leichter/einfacher Sprache"),
    NA("Sachgebiet unbekannt");

    private String description;

    Sachgebiet(String description) {
        this.description = description;
    }

    public String getName() {
        return name();
    }

    public String getDescription() {
        return description;
    }

    public String getLabel() {
        return String.format("%s - %s", name(), description);
    }

    @Override
    public String toString() {
        //return getLabel();
        return getName();
    }

}
