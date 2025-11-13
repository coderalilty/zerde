package kidd.house.zerde.config;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.context.annotation.Configuration;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

@Configuration
public class PdfCompressorConfig {

    /**
     * Сжимает PDF-файл, уменьшая качество изображений и убирая лишние метаданные.
     * @param sourcePath путь к исходному PDF
     * @param targetPath путь для сохранения сжатого PDF
     */
    public static void compressPdf(Path sourcePath, Path targetPath) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(sourcePath.toString());

        // Убираем ненужные метаданные
        reader.removeUnusedObjects();

        try (FileOutputStream fos = new FileOutputStream(targetPath.toFile())) {
            PdfStamper stamper = new PdfStamper(reader, fos, PdfWriter.VERSION_1_5);
            stamper.getWriter().setCompressionLevel(9); // 0–9, где 9 — максимальное сжатие

            // Включаем сжатие потоков
            stamper.setFullCompression();

            stamper.close();
        }
        reader.close();
    }
}
