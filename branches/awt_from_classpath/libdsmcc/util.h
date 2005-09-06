/***************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/


#ifndef LIBDSMCC_UTIL_H
#define LIBDSMCC_UTIL_H

#include <list>
#include <pthread.h>

//provide VDR logging stuff
#include <vdr/tools.h>
#define ilog(a...) isyslog(a)
#define dlog(a...) dsyslog(a)
#define elog(a...) esyslog(a)
//extreme debugging output ;-)
#define exdlog(a...)

unsigned long dsmcc_crc32 (unsigned char *data, int len);

//implements an array with bounds-checking
class RawData {
friend class CharArray;
public:
   RawData(unsigned char *data, unsigned int len) : buffer(data), length(len) {}
   RawData(const RawData &source) : buffer(source.buffer), length(source.length) {}
   
   //same as operator[] on a plain old array
   unsigned char operator[](const unsigned int index) const
      { return index<length ? buffer[index] : overflow(); }
      
   //returns a RawData object which starts at given index
   const RawData operator+(const unsigned int index) const
      { return index<length ? RawData(buffer+index, length-index) : RawData(0, 0); }
      
   //returns a pointer to data. Checks whether both index and length are legal, else returns 0
   // (which will most probably crash the program)
   unsigned char *getPointer(const unsigned int index, const unsigned int requestedLength) const
      { return (index<length && index+requestedLength<=length) ? &buffer[index] : overflowPointer() ; }
      
   //convenience method
   unsigned short TwoBytes(const unsigned int index) const
      { return index+1<length ? ( (buffer[index] << 8) | buffer[index+1] ) : overflow(); }
      
   //convenience method
   unsigned long FourBytes(const unsigned int index) const
      { return index+3<length ? 
         ( (buffer[index] << 24) | (buffer[index+1] << 16) | (buffer[index+2] << 8) | buffer[index+3] )
          : overflow(); }
private:
   unsigned char overflow()  const { return 0; }
   unsigned char *overflowPointer() const { return 0; }
   
   unsigned char *buffer;
   unsigned int length;
};

//implements a copy-on-write char array to allow fast and safe shallow copies
//taken and adapted from C++ Lite FAQ
//TODO: libsi has now a more mature CharArray (forked from this one here a long
//time ago). Replace all occurrences of this CharArray with SI::CharArray?
class CharArray {
public:
   CharArray();
   
   CharArray(const CharArray &source);
   CharArray& operator=(const CharArray &source);
   ~CharArray();
   
   void assign(unsigned char*data, unsigned int size);
   void assign(RawData data, unsigned int size);
   void assign(unsigned int size); //reserve memory of given size and fill with '0'
   //compares to a null-terminated string
   bool operator==(const char *string);   
   //compares to another CharArray (data not necessarily null-terminated)
   bool operator==(const CharArray &other);
   unsigned char* getData() const { return data_ ? data_->data : 0; }
   int getLength() const { return data_ ? data_->size : 0; }
private:
   class Data {
   public:
      Data();
      ~Data();
      Data(const Data& d);
      void assign(unsigned char*data, unsigned int size);
      void assign(unsigned int size);
      void Lock() {}
      void Unlock() {}
  
      unsigned char*data;
      unsigned int size;
  
      unsigned count_;
      // count_ is the number of CharArray objects that point at this
      // count_ must be initialized to 1 by all constructors
      // (it starts as 1 since it is pointed to by the Fred object that created it)
      
      //make it thread safe
      //I do not use VDR's thread library because I do not want to create a dependency
      /*pthread_mutex_t mutex;
      pid_t lockingPid;
      pthread_t locked;*/
   };  
   Data* data_;
};


//implements a char array which always uses shallow copying unless told to do a deep copy.
//ATTENTION: No reference counting! 
class SimpleCharArray {
public:
   SimpleCharArray(int size);
   //dummy object
   SimpleCharArray() { data=0; size=0; }
   
   SimpleCharArray(const SimpleCharArray &source);
   SimpleCharArray& operator=(const SimpleCharArray &source);
   
   //deep copy
   void deepCopy();
   //delete memory; destructor does _not_ delete memory
   void destroy();
   
   unsigned char* data;
   int size;
};

//simply makes sure that the buffer allocated 
//is deleted when the stack frame is left
class HeapCharArray {
public:
   HeapCharArray(uint size) { data=new unsigned char[size]; }
   ~HeapCharArray() { delete[] data; }
   operator unsigned char *() const { return data; }
   unsigned char operator[](const unsigned int index) const { return data[index]; }
private:
   unsigned char *data;
};

template <class T> class SmartPtr;

//classes given as template argument to SmartPtr<> must be derived from this class
class SmartPtrObject {
public:
   SmartPtrObject() : count_(0) {}
private:
   template <class T> friend class SmartPtr;
   unsigned count_;
   // count_ must be initialized to 0 by all constructors
   // count_ is the number of Ptr objects that point at this
};

//A smart Ptr to allow reference counting with pointers
//taken and adapted from C++ Lite FAQ
template <class T>
class SmartPtr {
public:
   T* operator-> () const { return p_; }
   T* getPointer() const { return p_; }
   T& operator* () const { return *p_; }
   SmartPtr(T* p)    : p_(p) { if (p_) ++p_->count_; }
   ~SmartPtr()           { if (p_ && --p_->count_ == 0) delete p_; }
   SmartPtr(const SmartPtr<T>& p) : p_(p.p_) { if (p_) ++p_->count_; }
   SmartPtr& operator= (const SmartPtr& p)
          { // DO NOT CHANGE THE ORDER OF THESE STATEMENTS!
            // (This order properly handles self-assignment)
            if (p.p_) ++p.p_->count_;
            if (p_ && --p_->count_ == 0) delete p_;
            p_ = p.p_;
            return *this;
          }
   SmartPtr& operator= (T *t)
          { // DO NOT CHANGE THE ORDER OF THESE STATEMENTS!
            // (This order properly handles self-assignment)
            if (t) ++t->count_;
            if (p_ && --p_->count_ == 0) delete p_;
            p_ = t;
            return *this;
          }
   operator bool() const { return p_ != 0; }
   bool operator==(const SmartPtr<T> &p) const { return p_==p.p_; }
   bool operator==(const T *const p) const { return p_==p; }
   bool operator!=(const SmartPtr<T> &p) const { return p_!=p.p_; }
      //for STL
   bool operator<(const SmartPtr<T> &p) const { return p_ < p.p_; }
   bool operator>(const SmartPtr<T> &p) const { return p_ > p.p_; }
private:
   T* p_;
};

// A simple bitset implementation. The difference to std::bitstream is that
// the size of this bitset is set at run-time.
class Bitset {
public:
   Bitset(int size) : size(size)
   {
      data = new unsigned char[(size / 8)+1];
      for (int i=0; i<(size / 8)+1; i++)
         data[i]=0;
   }
   ~Bitset()
     { delete[] data; }
   void Set(int index, bool value = true)
     { data[index/8]|=(1<<(index%8)); }
   bool isSet(int index) const
     { return (data[index/8] & (1<<(index%8))); }
   bool operator[](int index) const { return isSet(index); }
   bool isComplete() const
   {
      for (int i=0; i<(size / 8); i++)
         if (data[i] != static_cast<unsigned char>(~0))
            return false;
      for (int index=(size - (size%8)); index<size; index++)
         if (!isSet(index))
            return false;
      return true;
   }
   bool any() const
   {
      for (int i=0; i<(size / 8)+1; i++)
         if (data[i] != static_cast<unsigned char>(0))
            return true;
      return false;
   }
   bool none() const { return !any(); }
   int getSize() const { return size; }
protected:
   const int size;
   unsigned char *data;
};


//abstract base class
class Parsable {
protected:
   Parsable() {}
   virtual ~Parsable() {}
   
public:
   //parses given data. offset is only incremented, never set.
   virtual void Parse(RawData data, int &offset) = 0;
};


#endif

