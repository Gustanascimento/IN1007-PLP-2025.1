package li2.plp.imperative2.util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerConfig {
    private static final Logger logger = Logger.getLogger("MeuLogger");

    static {
        try {
            // Configura o FileHandler para gravar os logs em um arquivo
            FileHandler fileHandler = new FileHandler("logs.txt", true); // 'true' para adicionar ao arquivo
            fileHandler.setFormatter(new SimpleFormatter()); // Formato simples para os logs
            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false); // Evita que os logs sejam exibidos no console
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Logger getLogger() {
        return logger;
    }
}
