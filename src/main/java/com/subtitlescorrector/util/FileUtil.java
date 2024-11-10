package com.subtitlescorrector.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {

	private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

	public static List<String> loadTextFile(File file) {

		List<String> content = new ArrayList<String>();

		try {
		
			log.info("Starting to read file: " + file.getAbsolutePath());
			Charset detectedEncoding = detectEncodingOfFile(file);
			log.info("Detected encoding: " + detectedEncoding.displayName());
			
			Scanner scanner = new Scanner(file, detectedEncoding.displayName());
			
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
	
	/**
	 * Detects the encoding of a file using org.mozilla.universalchardet.UniversalDetector
	 * @param file
	 * @return detected charset if found, otherwise null
	 */
	public static Charset detectEncodingOfFile(File file) {

		UniversalDetector detector = new UniversalDetector(null);

		try (FileInputStream fis = new FileInputStream(file);) {

			byte[] buf = new byte[2048];
			if (fis != null) {
				int nread;
				while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
					detector.handleData(buf, 0, nread);
				}
			}

			detector.dataEnd();
			String charset = detector.getDetectedCharset();
			detector.reset();
			return Charset.forName(charset);

		} catch (Exception e) {
			log.error("Error during detecting file encoding!", e);
		}

		return null;

	}


}
