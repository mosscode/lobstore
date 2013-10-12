/**
 * Copyright (C) 2013, Moss Computing Inc.
 *
 * This file is part of lobstore.
 *
 * lobstore is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * lobstore is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with lobstore; see the file COPYING.  If not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under
 * terms of your choice, provided that you also meet, for each linked
 * independent module, the terms and conditions of the license of that
 * module.  An independent module is a module which is not derived from
 * or based on this library.  If you modify this library, you may extend
 * this exception to your version of the library, but you are not
 * obligated to do so.  If you do not wish to do so, delete this
 * exception statement from your version.
 */
package com.moss.lobstore;

import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.PrePersist;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Store;

import com.moss.lobstore.hibernate.search.UUIDStringBridge;
import com.moss.lobstore.jaxb.UUIDAdapter;

@XmlRootElement
@Entity(name="DocumentMetadata")
public class LobMetadata {
	private String name;
	private String mimeType;
	private Long size;
	
	public final String getName() {
		return name;
	}
	public final void setName(String name) {
		this.name = name;
	}
	public final String getMimeType() {
		return mimeType;
	}
	public final void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	public final Long getSize() {
		return size;
	}
	public final void setSize(Long size) {
		this.size = size;
	}
	
	
	public LobId id(){
		return new LobId(publicId);
	}
	
	/**
	 * BEGIN: CRAP I COPIED FROM THE ENTITY BOILERPLATE CLASSES
	 */
	
	@SuppressWarnings("unchecked")
	protected static <T extends LobMetadata> T get(LobId id, Class<T> entityClass, EntityManager entityManager){
		try {
			T application = (T) entityManager
			.createQuery("select entity from " + entityClass.getName() + " entity where publicId = :publicId")
			.setParameter("publicId", id.uuid())
			.getSingleResult();
			return application;
		} catch (NoResultException e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	protected static <T extends LobMetadata> List<T> list(Class<T> entityClass, EntityManager entityManager){
		return (List<T>) entityManager.createQuery("select p from " + entityClass.getName() + " p").getResultList();
	}
	
	@Deprecated
	@Id @GeneratedValue(generator="system-uuid")
	@Type(type="com.moss.hibernate.UUIDUserType")
	@GenericGenerator(name="system-uuid", strategy = "com.moss.hibernate.UUIDGenerator")
	@org.hibernate.search.annotations.DocumentId
	
	@FieldBridge(impl = UUIDStringBridge.class)
	private UUID id;
	
	@XmlElement @XmlJavaTypeAdapter(UUIDAdapter.class)
	@Type(type="com.moss.hibernate.UUIDUserType")
	@Field(
		index=Index.TOKENIZED, 
		store=Store.NO,
		bridge=@FieldBridge(impl = UUIDStringBridge.class)
	)
	@Column
	@org.hibernate.annotations.Index(name="PUBLIC_ID")
	private UUID publicId;
	
	public void generateIdentity(){
		if(this.publicId!=null)
			throw new Error("Identity already assigned");
		publicId = UUID.randomUUID();
	}
	
	public UUID jpaEntityId() {
		return id;
	}
	
	@Deprecated
	@PrePersist
	public void beforePersist() {
		if (publicId == null) {
			publicId = UUID.randomUUID();
		}
	}
}
