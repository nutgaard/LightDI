package no.utgdev.lightdi.exceptions;

import java.lang.reflect.Field;
import java.util.List;

public class LightDIFoundUnfulfilledBeans extends RuntimeException {
    public LightDIFoundUnfulfilledBeans() {
        this("LightDI found @Inject annotations which could not be fulfilled by the current beandefinitons.");
    }

    public LightDIFoundUnfulfilledBeans(String message) {
        super(message);
    }

    public static LightDIFoundUnfulfilledBeans create(List<Field> fields) {
        StringBuilder sb = new StringBuilder();
        sb.append("LightDI found @Inject annotations which could not be fulfilled by the current beandefinitons.\n");
        for (Field field : fields) {
                sb.append("Found no definition for: ").append(field.getType().getName()).append("\n");
        }
        return new LightDIFoundUnfulfilledBeans(sb.toString());
    }
}
