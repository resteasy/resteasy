/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.datatypes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Matrix<CellType> {
	
	private CellType[][] matrix;
	
	@SuppressWarnings("unchecked")
	public Matrix(int rows, int cols){
		this.matrix = (CellType[][])(new Object[rows][cols]);
	}
	
	public List<CellType> getRow(int rowPos){
		return Arrays.asList(this.matrix[rowPos]);
	}
	
	public List<CellType> getColumn(int columnPos){
		
		ArrayList<CellType> columnCells = new ArrayList<CellType>(this.getColumnCount());
		
		for(CellType[] row : this.matrix){
			columnCells.add(row[columnPos]);
		}
		
		return columnCells;
	}
	
	public CellType getCell(int rowPos , int colPos){
		return this.matrix[rowPos][colPos];
	}
	
	public void setCell(int rowPos , int colPos, CellType cell){
		this.matrix[rowPos][colPos] = cell;
	}
	
	public int getRowCount(){
		return this.matrix.length;
	}
	
	public int getColumnCount(){
		return this.matrix[0].length;
	}
}
