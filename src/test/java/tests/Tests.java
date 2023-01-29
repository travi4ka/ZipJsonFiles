package tests;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import models.MovieModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStreamReader;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Tests {

    @Test
    void readingZipFile() throws Exception {
        try (
                ZipInputStream zis = new ZipInputStream(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("test.zip")));
        ) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().toLowerCase().endsWith(".csv")) {
                    CSVReader reader = new CSVReader(new InputStreamReader(zis));
                    Assertions.assertEquals("2", reader.readAll().get(0)[1]);
                } else if (entry.getName().toLowerCase().endsWith(".pdf")) {
                    PDF content = new PDF(zis);
                    Assertions.assertTrue(content.text.contains("A Simple PDF File"));
                } else if (entry.getName().toLowerCase().contains(".xlsx")) {
                    XLS content = new XLS(zis);
                    Assertions.assertEquals("3.0", content.excel.getSheetAt(0).getRow(1).getCell(0).toString());
                }
            }
        }
    }

    @Test
    void parsingJsonFile() throws Exception {
        try (
                InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("movie.json")));
        ) {
            MovieModel movieModel = new ObjectMapper().readValue(reader, MovieModel.class);
            Assertions.assertEquals("Avatar", movieModel.title);
            Assertions.assertEquals(1990, (int) movieModel.year);
            Assertions.assertTrue(movieModel.isActive);
            Assertions.assertArrayEquals(new String[]{"Actor 1", "Actor 2"}, movieModel.actors);
        }
    }
}