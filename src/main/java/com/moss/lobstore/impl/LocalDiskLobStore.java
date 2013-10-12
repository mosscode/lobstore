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
package com.moss.lobstore.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.joda.time.Instant;

import com.moss.lobstore.LobId;
import com.moss.lobstore.LobStore;

public class LocalDiskLobStore implements LobStore {
	private final int bufferSize = 100*1024; //100k
	File documentsDirLocation;
	
	public LocalDiskLobStore(File documentsDirLocation) {
		super();
		this.documentsDirLocation = documentsDirLocation;
	}

	public File path(LobId id){
		if (id == null) {
			throw new NullPointerException("Null id not permitted");
		}
		
		if(!documentsDirLocation.exists()) documentsDirLocation.mkdirs();
		File documentFile = new File(documentsDirLocation, id.toString());
		return documentFile;
	}
	
	public long length(LobId id) {
		return path(id).length();
	}
	
	public void put(LobId id, byte[] data){
		try {
			OutputStream out = new FileOutputStream(path(id));
			try {
				out.write(data);
				out.flush();
			} finally {
				out.close();
			}
		} catch (IOException e) {
			throw new Error(e);
		}
		
	}
	
	public void put(LobId id, InputStream data) {
		try {
			try {
				OutputStream out = new FileOutputStream(path(id));
				try {
					byte[] b = new byte[bufferSize];
					for(int numRead=data.read(b);numRead!=-1;numRead = data.read(b)){
						out.write(b, 0, numRead);
					}
					out.flush();
				} finally {
					out.close();
				}
			} finally {
				data.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void remove(LobId id) {
		
		File path = path(id);
		
		if (path.exists() && !path.delete()) {
			throw new Error("Could not delete: " + path);
		}
	}
	
	public boolean exists(LobId id) {
		return path(id).exists();
	}
	
	public InputStream getStream(LobId id) {
		File documentFile = path(id);
		try {
			InputStream in = new FileInputStream(documentFile);
			return in;
		}
		catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public byte[] getBytes(LobId id){
		File documentFile = path(id);
		
		try {
			ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
			InputStream in = new FileInputStream(documentFile);
			byte[] buffer = new byte[1024*100];
			for(int numRead = in.read(buffer);numRead!=-1;numRead = in.read(buffer)){
				bytesOut.write(buffer, 0 ,numRead);
			}
			in.close();
			bytesOut.close();
			
			return bytesOut.toByteArray();
			
		} catch (IOException e) {
			throw new Error(e);
		}
	}
	
	public Instant lastModified(LobId id){
        return new Instant(path(id).lastModified());
    }
}
