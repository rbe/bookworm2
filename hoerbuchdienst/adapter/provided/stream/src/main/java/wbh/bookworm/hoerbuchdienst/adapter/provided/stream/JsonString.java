package wbh.bookworm.hoerbuchdienst.adapter.provided.stream;

public class JsonString {

    private final String name;

    private final String string;

    public JsonString(final String name, final String string) {
        this.name = name;
        this.string = string;
    }

    public String getName() {
        return name;
    }

    public String getString() {
        return string;
    }

    @Override
    public String toString() {
        return String.format("JsonString{name='%s', string='%s'}", name, string);
    }

}
