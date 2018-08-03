package ui;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.io.Serializable;

@Component
@RequestScope
public class Blub implements Serializable {

    public String sayHello() {
        return "Hello " + this;
    }

}
