package com.subtitlescorrector.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {

	private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

	public static List<String> loadTextFile(File file) {

		List<String> content = new ArrayList<String>();

		try {
			log.info("Starting to read file: " + file.getAbsolutePath());
			Scanner scanner = new Scanner(file);
			while (scanner.hasNext()) {
				content.add(scanner.nextLine());
			}

			scanner.close();

		} catch (FileNotFoundException e) {
			log.error("Error reading file!", e);
		}

		return content;
	}
	
	public static void writeLinesToFile(File file, List<String> correctedFileLines, Charset charset) {

		try (Writer fileWriter = new OutputStreamWriter(new FileOutputStream(file), charset)) {

			for (String line : correctedFileLines) {

				fileWriter.append(line + System.lineSeparator());
			}

		} catch (IOException e) {
			log.error("Error writing to file!", e);
		}

	}

}
