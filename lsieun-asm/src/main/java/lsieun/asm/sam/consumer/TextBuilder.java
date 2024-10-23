package lsieun.asm.sam.consumer;

import org.objectweb.asm.Type;

public interface TextBuilder {
    interface Init {
        WithAppend init();
    }

    interface WithAppend {
        WithAppend append(String str);

        WithAppend append(MethodVisitorConsumer consumer, Type type);

        WithAppend appendValueOnStack(Type t);
    }

    interface ToText {
        void toText();
    }
}
