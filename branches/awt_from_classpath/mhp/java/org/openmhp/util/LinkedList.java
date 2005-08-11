package org.openmhp.util;

/**
 * This is a LinkedList implementation for Java environments that don't
 * support Sun's <code>LinkedList</code> -class.
 *
 *
 * @author tejopa
 * @date 12.2.2004
 * @status fully implemented
 * @module internal
 */
public class LinkedList	{


	// list for objects
	Object[] list = null;

	// size
	int size;

	// index
	int index;

	public LinkedList()	{
		list = new Object[size];
		size=0;
	}

	public void add(Object o)	{
		size++;
		Object[] temp = new Object[size];
		// for each object in current list

		for (int i=0;i<size-1;i++)		{
			// copy original to temp
			temp[i]=list[i];
		}
		// add new object into temp
		temp[size-1]=o;
		list = temp;
		// require
		// size == old_size + 1
		// list[size]== o
	}

	public void addFirst(Object o)	{
		size++;
		Object[] temp = new Object[size];
		// for each object in current list
		for (int i=0;i<size-1;i++)		{
			// copy original to temp
			temp[i+1]=list[i];
		}
		// add new object into temp
		temp[0]=o;
		list=temp;
		// require
		// size == old_size + 1
		// list[0]== o
	}

	public Object removeFirst()	{
		Object first = getFirst();
		size--;
		Object[] temp = new Object[size];
		// for each object in current list
		for (int i=1;i<size+1;i++)		{
			// copy original to temp
			temp[i-1]=list[i];
		}
		// add new object into temp
		list=temp;
		return first;
		// require
		// size == old_size + 1
		// list[0]== o
	}

	public Object removeLast()	{
		Object last = getLast();
		size--;
		Object[] temp = new Object[size];
		// for each object in current list
		for (int i=0;i<size;i++)		{
			// copy original to temp
			temp[i]=list[i];
		}
		// add new object into temp
		list=temp;
		return last;
		// require
		// size == old_size + 1
		// list[0]== o
	}

	public void remove(int index)	{
		int k=0;
		Object removed = list[index];
		if (size-1>=0) {
			Object[] temp = new Object[size-1];
			// for each object in current list
			for (int i=0;i<size;i++)		{
				if (i!=index)			{
					// copy original to temp
					temp[k]=list[i];
					k++;
				}
			}
			list=temp;
			size--;
		}
	}

	public void addLast(Object o)	{
		add(o);
	}

	public void addTo(int index, Object o)	{
		size++;
		Object[] temp = new Object[size];
		// for each object before index in current list
		for (int i=0;i<index;i++)
		{
			// copy original to temp
			temp[i]=list[i];
		}

		// add new object into temp
		temp[index]=o;

		// for each object after index in current list
		for (int i=index;i<size-1;i++)
		{
			// copy original to temp
			temp[i+1]=list[i];
		}
		list = temp;
		// require
		// size == old_size + 1
		// list[size]== o
	}


	public Object get(int i)	{
		return list[i];
	}

	public Object getFirst()	{
		return list[0];
	}

	public Object getLast()	{
		return list[size-1];
	}

	public int size()	{
		return size;
	}

	public void remove(Object o)	{
		int k=0;
		if (size-1>=0) {
				Object[] temp = new Object[size-1];
			// for each object in current list
			for (int i=0;i<size;i++)		{
				if (list[i]!=o)			{
					// copy original to temp
					temp[k]=list[i];
					k++;
				}
			}
			// add new object into temp
			list=temp;
			size--;
		}
		// require:
		// size == old_size - 1 AND 0<i<size: list[i]!=o
	}

	public void clear()	{
		size=0;
		Object[] temp = new Object[size];
		list = temp;
	}


	/**
	* THIS MAY NOT WORK... ?! HAVENT TRIED IT EVER
	*/
	public void addAll(int start, LinkedList source)	{
		for (int i=0;i<source.size();i++)
		{
			addTo(start,source.get(i));
			start++;
		}
	}

	/**
	* Append argument list to the end of THIS list.
	*
	*/
	public void append(LinkedList source) {
		for (int i=0;i<source.size();i++) {
			this.add(source.get(i));
		}
	}

	public void remove(LinkedList source) {
		for (int i=0;i<source.size();i++) {
			this.remove(source.get(i));
		}
	}


/**
* Method returns a boolean value for question "does this
* structure contain argument object".
* @return <code>boolean</code> value for question "is argument in this list?".
*/
	public boolean has(Object object) {
		boolean result = false;
		int i = 0;
		while (!result && i<size() ) {
			if (get(i)==object) result = true;
			i++;
		}
		return result;
	}

/**
* Returns index for the first occurrence of argument object.
* @author tejopa
* @date 2002/21/11
*/
	public int indexOf(Object object) {
		int result = -1;
		int i = 0;
		while (result!=-1 && i<size() ) {
			if (get(i)==object) result = i;
			i++;
		}
		return result;
	}

	public Object[] getElements() {
		return list;
	}


} // end class LinkedList
