package shop.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import shop.command.CommandHistory;
import shop.command.CommandHistoryFactory;

/**
 * Implementation of Inventory interface.
 * 
 * @see Data
 */
final class InventorySet implements Inventory {
	private Map<Video, Record> _data;
	private final CommandHistory _history;

	InventorySet() {
		// TODO
		_data = new HashMap<Video, Record>();
		_history = CommandHistoryFactory.newCommandHistory();

	}

	/**
	 * If <code>record</code> is null, then delete record for <code>video</code>;
	 * otherwise replace record for <code>video</code>.
	 */
	void replaceEntry(Video video, Record record) {
		_data.remove(video);
		if (record != null)
			_data.put(video, ((RecordObj) record).copy());
	}

	/**
	 * Overwrite the map.
	 */
	void replaceMap(Map<Video, Record> data) {
		_data = data;
	}

	public int size() {
		// TODO
		return _data.size();
	}

	public Record get(Video v) {
		// TODO
		return _data.get(v);
	}

	public Iterator<Record> iterator() {
		return Collections.unmodifiableCollection(_data.values()).iterator();
	}

	public Iterator<Record> iterator(Comparator<Record> comparator) {
		// TODO
		Collection<Record> dval = _data.values();
		List<Record> listOfRecords = new ArrayList<Record>(dval);
		Collections.sort(listOfRecords, comparator);
		return Collections.unmodifiableList(listOfRecords).iterator();
	}

	/**
	 * Add or remove copies of a video from the inventory. If a video record is not
	 * already present (and change is positive), a record is created. If a record is
	 * already present, <code>numOwned</code> is modified using <code>change</code>.
	 * If <code>change</code> brings the number of copies to be less than one, the
	 * record is removed from the inventory.
	 * 
	 * @param video  the video to be added.
	 * @param change the number of copies to add (or remove if negative).
	 * @return A copy of the previous record for this video (if any)
	 * @throws IllegalArgumentException if video null or change is zero
	 */
	Record addNumOwned(Video video, int change) {
		if (video == null || change == 0)
			throw new IllegalArgumentException();

		RecordObj r = (RecordObj) _data.get(video); 
		if (r == null && change < 1) {
			throw new IllegalArgumentException();
		} else if (r == null) {
			_data.put(video, new RecordObj(video, change, 0, 0));
		} else if (r.numOwned + change < r.numOut) {
			throw new IllegalArgumentException();
		} else if (r.numOwned + change < 1) {
			_data.remove(video);
		} else {
			_data.put(video, new RecordObj(video, r.numOwned + change, r.numOut, r.numRentals));
		}
		return r;
	}

	/**
	 * Check out a video.
	 * 
	 * @param video the video to be checked out.
	 * @return A copy of the previous record for this video
	 * @throws IllegalArgumentException if video has no record or numOut equals
	 *                                  numOwned.
	 */

	Record checkOut(Video video) {
		// TODO
		RecordObj recordObj = (RecordObj) this._data.get(video);
		if (recordObj == null || recordObj.numOut() == recordObj.numOwned()) {
			throw new IllegalArgumentException("Video has no record or is fully checked out");
		}
		// return previous record copy
		RecordObj prevRecord = recordObj.copy();

		// replace entry with updated record
		recordObj.numOut = recordObj.numOut + 1;
		recordObj.numRentals = recordObj.numRentals + 1;
		_data.replace(video, new RecordObj(video, recordObj.numOwned, recordObj.numOut, recordObj.numRentals));
		return prevRecord;
	}

	/**
	 * Check in a video.
	 * 
	 * @param video the video to be checked in.
	 * @return A copy of the previous record for this video
	 * @throws IllegalArgumentException if video has no record or numOut
	 *                                  non-positive.
	 */
	Record checkIn(Video video) {
		// TODO
		RecordObj recordObj = (RecordObj) this._data.get(video);
		if (recordObj == null || recordObj.numOut <= 0) {
			throw new IllegalArgumentException("Video has no record or numOut is non-positive");
		}
		// return previous record copy
		Record prevRecord = recordObj.copy();

		// replace entry with updated record
		recordObj.numOut = recordObj.numOut - 1;
		_data.replace(video, new RecordObj(video, recordObj.numOwned, recordObj.numOut, recordObj.numRentals));
		return prevRecord;
	}

	/**
	 * Remove all records from the inventory.
	 * 
	 * @return A copy of the previous inventory as a Map
	 */
	Map<Video, Record> clear() {
		// TODO
		Map<Video, Record> previousInventory = new HashMap<Video, Record>(_data);
		_data.clear();
		return previousInventory;
	}

	/**
	 * Return a reference to the history.
	 */
	CommandHistory getHistory() {
		// TODO
		return _history;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Database:\n");
		Iterator<Record> i = _data.values().iterator();
		while (i.hasNext()) {
			buffer.append("  ");
			buffer.append(i.next());
			buffer.append("\n");
		}
		return buffer.toString();
	}

	/**
	 * Implementation of Record interface.
	 *
	 * <p>
	 * This is a utility class for Inventory. Fields are mutable and
	 * package-private.
	 * </p>
	 *
	 * <p>
	 * <b>Class Invariant:</b> No two instances may reference the same Video.
	 * </p>
	 *
	 * @see Record
	 */
	private static final class RecordObj implements Record {
		Video video; // the video
		int numOwned; // copies owned
		int numOut; // copies currently rented
		int numRentals; // total times video has been rented

		RecordObj(Video video, int numOwned, int numOut, int numRentals) {
			this.video = video;
			this.numOwned = numOwned;
			this.numOut = numOut;
			this.numRentals = numRentals;
		}

		RecordObj copy() {
			return new RecordObj(video, numOwned, numOut, numRentals);
		}

		public Video video() {
			return video;
		}

		public int numOwned() {
			return numOwned;
		}

		public int numOut() {
			return numOut;
		}

		public int numRentals() {
			return numRentals;
		}

		public boolean equals(Object thatObject) {
			return video.equals(((Record) thatObject).video());
		}

		public int hashCode() {
			return video.hashCode();
		}

		public String toString() {
			StringBuffer buffer = new StringBuffer();
			buffer.append(video);
			buffer.append(" [");
			buffer.append(numOwned);
			buffer.append(",");
			buffer.append(numOut);
			buffer.append(",");
			buffer.append(numRentals);
			buffer.append("]");
			return buffer.toString();
		}
	}
}
