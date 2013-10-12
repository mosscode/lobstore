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

import java.io.InputStream;

import org.joda.time.Instant;

import com.moss.bdbwrap.DbWrap;
import com.moss.lobstore.LobId;
import com.moss.lobstore.LobMetadata;
import com.moss.lobstore.LobRepository;
import com.moss.lobstore.LobStore;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.Transaction;

public class BDBFilesDocumentPersister <T extends LobMetadata> implements LobRepository<T> {
		private LobStore lobStore;
		private DbWrap<LobId, T> metadata;
		
		public BDBFilesDocumentPersister(LobStore lobStore, DbWrap<LobId, T> metadata) {
			super();
			this.lobStore = lobStore;
			this.metadata = metadata;
		}

		public boolean exists(LobId id) {
			return lobStore.exists(id);
		}

		public byte[] getBytes(LobId id) {
			return lobStore.getBytes(id);
		}
		
		public T getMetadata(LobId id, Transaction t){
			return metadata.get(id, t, LockMode.DEFAULT);
		}
		public void updateMetadata(T metadata, Transaction t) {
			this.metadata.put(metadata.id(), metadata, t);
		};

		public InputStream getStream(LobId id) {
			return lobStore.getStream(id);
		}

		public Instant lastModified(LobId id) {
			return lobStore.lastModified(id);
		}

		public void put(T md, byte[] data, Transaction t) {
			lobStore.put(md.id(), data);
			metadata.put(md.id(), md, t);
		}

		public void remove(LobId id, Transaction t) {
			metadata.delete(id, t);
			lobStore.remove(id);
		}
		
		public LobStore underlyingStore() {
			return lobStore;
		}
}
