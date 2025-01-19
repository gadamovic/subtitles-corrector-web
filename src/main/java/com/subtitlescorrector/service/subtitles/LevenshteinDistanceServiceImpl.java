package com.subtitlescorrector.service.subtitles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.subtitlescorrector.domain.CompositeEditOperation;
import com.subtitlescorrector.domain.EditOperation;
import com.subtitlescorrector.domain.EditOperation.OperationType;
import com.subtitlescorrector.util.Util;

/**
 * Service that utilizes Levenshtein distance algorithm to calculate differences
 * between strings https://en.wikipedia.org/wiki/Levenshtein_distance
 * 
 * @author Gavrilo Adamovic
 *
 */
@Service
public class LevenshteinDistanceServiceImpl implements EditDistanceService {

	/**
	 * Returns all edit operations including 'keep' (no change) operation resulting
	 * in the entire target string
	 * 
	 * @param source
	 * @param target
	 * @return
	 */
	@Override
	public List<EditOperation> getEditOperations(String source, String target) {

		List<EditOperation> editOperations = new ArrayList<>();

		int[][] dpTable = generateLevenshteinDpTable(source, target);
		// print(dpTable, source.length(), target.length());

		int i = source.length();
		int j = target.length();

		int strI = i - 1;
		int strJ = j - 1;

		while (i != 0 || j != 0) {

			int replacementOrNoChange = 99999; // diagonal
			int insertCell = 99999; // left
			int deleteCell = 99999; // up

			if (i > 0 && j > 0) {
				replacementOrNoChange = dpTable[i - 1][j - 1];
			}

			if (j > 0) {
				insertCell = dpTable[i][j - 1];
			}

			if (i > 0) {
				deleteCell = dpTable[i - 1][j];
			}

			int smallest = min(insertCell, replacementOrNoChange, deleteCell);

			if (replacementOrNoChange == smallest) {

				if (source.charAt(strI) != target.charAt(strJ)) {
					addOperationToList(editOperations, source.charAt(strI), target.charAt(strJ), OperationType.REPLACE);
				} else {
					addOperationToList(editOperations, source.charAt(strI), null, OperationType.KEEP);
				}

				i--;
				j--;
				strI--;
				strJ--;

			} else if (insertCell == smallest) {
				addOperationToList(editOperations, target.charAt(strJ), null, OperationType.ADD);
				j--;
				strJ--;
			} else if (deleteCell == smallest) {
				addOperationToList(editOperations, source.charAt(strI), null, OperationType.DELETE);
				i--;
				strI--;
			}

		}

		Collections.reverse(editOperations);
		return editOperations;
	}

	private void addOperationToList(List<EditOperation> editOperations, Character char1, Character char2,
			OperationType operationType) {

		EditOperation operation = new EditOperation();
		operation.setChar1(char1);
		operation.setChar2(char2);
		operation.setType(operationType);
		editOperations.add(operation);
	}

	private int[][] generateLevenshteinDpTable(String source, String target) {
		int[][] dpTable = initDpTable(source.length(), target.length());

		for (int i = 0; i < source.length(); i++) {
			for (int j = 0; j < target.length(); j++) {

				int dpI = i + 1;
				int dpJ = j + 1;

				int insertCell = dpTable[dpI][dpJ - 1]; // left
				int replacementOrNoChange = dpTable[dpI - 1][dpJ - 1]; // diagonal
				int deleteCell = dpTable[dpI - 1][dpJ]; // up

				int smallest = min(insertCell, replacementOrNoChange, deleteCell);

				if (source.charAt(i) == target.charAt(j)) {

					dpTable[dpI][dpJ] = dpTable[dpI - 1][dpJ - 1];

				} else {

					if (insertCell == smallest) {
						dpTable[dpI][dpJ] = smallest + 1;
					} else if (replacementOrNoChange == smallest) {
						dpTable[dpI][dpJ] = smallest + 1;
					} else if (smallest == deleteCell) {
						dpTable[dpI][dpJ] = smallest + 1;
					}

				}

			}
		}
		return dpTable;
	}

	private void print(int[][] dpTable, int dimI, int dimJ) {

		for (int i = 0; i <= dimI; i++) {
			for (int j = 0; j <= dimJ; j++) {
				System.out.print(dpTable[i][j]);
			}
			System.out.println();
		}

	}

	private int min(int insertCell, int replacementOrNoChange, int deleteCell) {

		List<Integer> sorted = Arrays.asList(insertCell, replacementOrNoChange, deleteCell);
		sorted.sort((a, b) -> a > b ? 1 : -1);

		return sorted.get(0);
	}

	private int[][] initDpTable(int dimX, int dimY) {

		int[][] table = new int[dimX + 1][dimY + 1];

		for (int i = 0; i <= dimX; i++) {
			table[i][0] = i;
		}

		for (int i = 0; i <= dimY; i++) {
			table[0][i] = i;
		}

		return table;
	}

	public static void main(String args[]) {

		LevenshteinDistanceServiceImpl service = new LevenshteinDistanceServiceImpl();
		List<EditOperation> operations = service.getEditOperations("<b>text</b>", "tekst");

		List<List<EditOperation>> grouped = Util.groupConsecutiveEditOperations(operations);
		List<CompositeEditOperation> composite = Util.groupConsecutiveEditOperations2(operations);
		
//		for (int i = 0; i < grouped.size(); i++) {
//
//			List<EditOperation> innerList = grouped.get(i);
//
//			for (int j = 0; j < innerList.size(); j++) {
//				System.out.println(innerList.get(j));
//			}
//
//			System.out.println();
//
//		}

		for(CompositeEditOperation comp : composite) {
			System.out.println(comp);
		}
		
		
	}

}
