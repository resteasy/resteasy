/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import se.unlogic.standardutils.annotations.NoAnnotatedFieldsFoundException;
import se.unlogic.standardutils.arrays.ArrayUtils;
import se.unlogic.standardutils.date.DateUtils;
import se.unlogic.standardutils.reflection.ReflectionUtils;
import se.unlogic.standardutils.string.DummyStringyfier;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.string.Stringyfier;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class XMLGenerator {

	//TODO Use weak references in this map to prevent memory leaks
	private static ConcurrentHashMap<Class<?>, ClassXMLInfo> FIELD_MAP = new ConcurrentHashMap<Class<?>, ClassXMLInfo>();

	@SuppressWarnings("unchecked")
	public static Element toXML(Object bean, Document doc){

		ClassXMLInfo classInfo = FIELD_MAP.get(bean.getClass());

		if(classInfo == null){

			Class<?> clazz = bean.getClass();

			XMLElement xmlElement = clazz.getAnnotation(XMLElement.class);

			if(xmlElement == null){

				throw new MissingXMLAnnotationException(clazz);
			}

			String elementName = xmlElement.name();

			if(StringUtils.isEmpty(elementName)){

				elementName = clazz.getSimpleName();
			}

			List<FieldXMLInfo> annotatedFields = new ArrayList<FieldXMLInfo>();

			Class<?> currentClazz = clazz;

			while (currentClazz != Object.class) {

				Field[] fields = currentClazz.getDeclaredFields();

				for(Field field : fields){

					XMLElement elementAnnotation = field.getAnnotation(XMLElement.class);

					if(elementAnnotation != null){

						String name = elementAnnotation.name();

						if(StringUtils.isEmpty(name)){

							name = field.getName();
						}

						Stringyfier valueFormatter = null;

						if(elementAnnotation.valueFormatter() != DummyStringyfier.class){

							try {
								valueFormatter = elementAnnotation.valueFormatter().newInstance();

							} catch (InstantiationException e) {

								throw new RuntimeException(e);

							} catch (IllegalAccessException e) {

								throw new RuntimeException(e);
							}
						}

						if(Collection.class.isAssignableFrom(field.getType())){

							boolean elementable = false;

							if(ReflectionUtils.isGenericlyTyped(field) && Elementable.class.isAssignableFrom((Class<?>) ReflectionUtils.getGenericType(field))){

								elementable = true;
							}

							String childName = elementAnnotation.childName();

							if(StringUtils.isEmpty(childName)){

								childName = "value";
							}

							annotatedFields.add(new FieldXMLInfo(name, field, FieldType.Element,elementAnnotation.cdata(),elementable,true,false,childName,elementAnnotation.skipChildParentElement(),valueFormatter));

						}else if(field.getType().isArray()){

							boolean elementable = false;

							if(Elementable.class.isAssignableFrom(field.getType())){

								elementable = true;
							}

							String childName = elementAnnotation.childName();

							if(StringUtils.isEmpty(childName)){

								childName = "value";
							}

							annotatedFields.add(new FieldXMLInfo(name, field, FieldType.Element,elementAnnotation.cdata(),elementable,false,true,childName,elementAnnotation.skipChildParentElement(),valueFormatter));

						}else{

							String childName = null;

							if(!StringUtils.isEmpty(elementAnnotation.childName())){

								childName = elementAnnotation.childName();
							}

							annotatedFields.add(new FieldXMLInfo(name, field, FieldType.Element,elementAnnotation.cdata(),Elementable.class.isAssignableFrom(field.getType()),false,false,childName,false,valueFormatter));
						}

						ReflectionUtils.fixFieldAccess(field);
					}

					XMLAttribute attributeAnnotation = field.getAnnotation(XMLAttribute.class);

					if(attributeAnnotation != null){

						String name = attributeAnnotation.name();

						if(StringUtils.isEmpty(name)){

							name = field.getName();
						}

						Stringyfier valueFormatter = null;

						if(attributeAnnotation.valueFormatter() != DummyStringyfier.class){

							try {
								valueFormatter = attributeAnnotation.valueFormatter().newInstance();

							} catch (InstantiationException e) {

								throw new RuntimeException(e);

							} catch (IllegalAccessException e) {

								throw new RuntimeException(e);
							}
						}

						annotatedFields.add(new FieldXMLInfo(name, field, FieldType.Attribute,false,false,false,false,null,false,valueFormatter));

						ReflectionUtils.fixFieldAccess(field);
					}
				}

				currentClazz = currentClazz.getSuperclass();
			}

			if(annotatedFields.isEmpty()){

				throw new NoAnnotatedFieldsFoundException(clazz,XMLElement.class,XMLAttribute.class);
			}

			classInfo = new ClassXMLInfo(elementName, annotatedFields);

			FIELD_MAP.put(clazz, classInfo);
		}

		Element classElement = doc.createElement(classInfo.getElementName());

		for(FieldXMLInfo fieldInfo : classInfo.getFields()){

			Object fieldValue;
			try {
				fieldValue = fieldInfo.getField().get(bean);
			} catch (IllegalArgumentException e) {

				throw new RuntimeException(e);

			} catch (IllegalAccessException e) {

				throw new RuntimeException(e);
			}

			if(fieldValue == null){

				continue;

			}else if(!fieldInfo.isList() && fieldInfo.getValueFormatter() != null){

				fieldValue = fieldInfo.getValueFormatter().format(fieldValue);

			}else if(fieldValue instanceof Date){

				fieldValue = DateUtils.DATE_TIME_FORMATTER.format(fieldValue);
			}

			if(fieldInfo.getFieldType() == FieldType.Attribute){

				classElement.setAttribute(fieldInfo.getName(), fieldValue.toString());

			}else if(fieldInfo.isList()){

				List<?> fieldValues = (List<?>)fieldValue;

				if(fieldValues.isEmpty()){

					continue;
				}

				Element subElement;
				
				if(fieldInfo.skipSubElement()){
					
					subElement = classElement;
					
				}else{
					
					subElement = doc.createElement(fieldInfo.getName());
				}


				for(Object value : fieldValues){

					if(value != null){

						parseValue(fieldInfo,value,subElement,doc);
					}
				}

				if(!fieldInfo.skipSubElement() && subElement.hasChildNodes()){
					classElement.appendChild(subElement);
				}

			}else if(fieldInfo.isArray()){

				Object[] fieldValues = (Object[])fieldValue;

				if(ArrayUtils.isEmpty(fieldValues)){

					continue;
				}

				Element subElement;
				
				if(fieldInfo.skipSubElement()){
					
					subElement = classElement;
					
				}else{
					
					subElement = doc.createElement(fieldInfo.getName());
				}


				for(Object value : fieldValues){

					if(value != null){

						parseValue(fieldInfo,value,subElement,doc);
					}
				}

				if(!fieldInfo.skipSubElement() && subElement.hasChildNodes()){
					classElement.appendChild(subElement);
				}

			}else if(fieldInfo.isElementable()){

				Element subElement = ((Elementable)fieldValue).toXML(doc);

				if(subElement != null){

					if(fieldInfo.getChildName() != null){

						Element middleElement = doc.createElement(fieldInfo.getChildName());
						classElement.appendChild(middleElement);
						middleElement.appendChild(subElement);

					}else{
						classElement.appendChild(subElement);
					}
				}
			}else if(fieldInfo.isCDATA()){

				classElement.appendChild(XMLUtils.createCDATAElement(fieldInfo.getName(), fieldValue.toString(), doc));

			}else{
				classElement.appendChild(XMLUtils.createElement(fieldInfo.getName(), fieldValue.toString(), doc));
			}
		}

		return classElement;
	}

	private static void parseValue(FieldXMLInfo fieldInfo, Object value, Element subElement, Document doc) {

		if(fieldInfo.getValueFormatter() != null){

			value = fieldInfo.getValueFormatter().format(value);

		}else if(value instanceof Date){

			value = DateUtils.DATE_TIME_FORMATTER.format(value);
		}

		if(fieldInfo.isElementable()){

			Element subSubElement = ((Elementable)value).toXML(doc);

			if(subSubElement != null){

				subElement.appendChild(subSubElement);
			}
		}else{

			if(fieldInfo.isCDATA()){

				subElement.appendChild(XMLUtils.createCDATAElement(fieldInfo.getChildName(), value, doc));

			}else{

				subElement.appendChild(XMLUtils.createElement(fieldInfo.getChildName(), value, doc));
			}
		}
	}
}
