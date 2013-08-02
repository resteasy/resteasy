/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class RelationQuery {

	private List<Field> relations;
	private List<Field> excludedRelations;

	private boolean disableAutoRelations;
	
	public RelationQuery() {}

	public RelationQuery(List<Field> relations) {

		this.relations = relations;
	}

	public RelationQuery(Field... relations) {

		this.addRelations(relations);
	}

	public RelationQuery(RelationQuery relationQuery) {

		this.addRelations(relationQuery);
	}

	public List<Field> getRelations() {

		return relations;
	}

	public void setRelations(List<Field> relations) {

		this.relations = relations;
	}

	public void addRelation(Field relation){

		if(this.relations == null){

			this.relations = new ArrayList<Field>();
		}

		this.relations.add(relation);
	}

	public void addRelations(Field... relations){

		if(this.relations == null){

			this.relations = new ArrayList<Field>();
		}

		this.relations.addAll(Arrays.asList(relations));
	}

	public static boolean hasRelations(RelationQuery query){

		if(query == null || query.getRelations() == null || query.getRelations().isEmpty()){
			return false;
		}

		return true;
	}

	public boolean hasRelations(){

		return hasRelations(this);
	}

	public void addRelations(RelationQuery relationQuery) {

		if(hasRelations(relationQuery)){

			this.addRelations(relationQuery.getRelations());
		}
	}

	public void addRelations(List<Field> relations){

		if(this.relations == null){

			this.relations = relations;

		}else{

			this.relations.addAll(relations);
		}
	}

	public List<Field> getExcludedRelations() {

		return excludedRelations;
	}

	public void setExcludedRelations(List<Field> excludedRelations) {

		this.excludedRelations = excludedRelations;
	}

	public void addExcludedRelation(Field relation){

		if(this.excludedRelations == null){

			this.excludedRelations = new ArrayList<Field>();
		}

		this.excludedRelations.add(relation);
	}

	public void addExcludedRelations(Field... excludedRelations){

		if(this.excludedRelations == null){

			this.excludedRelations = new ArrayList<Field>();
		}

		this.excludedRelations.addAll(Arrays.asList(excludedRelations));
	}

	public static boolean hasExcludedRelations(RelationQuery query){

		if(query == null || query.getExcludedRelations() == null || query.getExcludedRelations().isEmpty()){
			return false;
		}

		return true;
	}

	public boolean hasExcludedRelations(){

		return hasExcludedRelations(this);
	}

	public void addExcludedRelations(RelationQuery relationQuery) {

		if(hasExcludedRelations(relationQuery)){

			this.addExcludedRelations(relationQuery.getExcludedRelations());
		}
	}

	public void addExcludedRelations(List<Field> excludedRelations){

		if(this.excludedRelations == null){

			this.excludedRelations = excludedRelations;

		}else{

			this.excludedRelations.addAll(excludedRelations);
		}
	}

	public boolean containsRelation(Field field) {

		if(this.relations != null){

			return this.relations.contains(field);
		}

		return false;
	}

	public boolean containsExcludedRelation(Field field) {

		if(this.excludedRelations != null){

			return this.excludedRelations.contains(field);
		}

		return false;
	}

	
	public boolean isDisableAutoRelations() {
	
		return disableAutoRelations;
	}

	
	public void disableAutoRelations(boolean disableAutoRelations) {
	
		this.disableAutoRelations = disableAutoRelations;
	}
}
