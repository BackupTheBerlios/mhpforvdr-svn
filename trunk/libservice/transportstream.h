/***************************************************************************
 *       Copyright (c) 2005 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#ifndef LIBSERVICE_TRANSPORTSTREAM_H
#define LIBSERVICE_TRANSPORTSTREAM_H

class cChannel;

namespace Service {

//Two simple structures for TS and Service. Can be passed as value.
class TransportStreamID {
public:
   TransportStreamID(int s, int n, int t) : source(s), onid(n), tid(t) {}
   TransportStreamID() : source(0), onid(0), tid(0) {}
   int source;
   int onid;
   int tid;
   bool operator==(const TransportStreamID &other) const
      { return tid==other.tid && onid==other.onid && source==other.source; }
   bool operator!=(const TransportStreamID &other) const
      { return !operator==(other); }
   bool equals(int s, int n ,int t) const
      { return tid==t && onid==n && source==s; }
   //default copy constructor and operator= are sufficient
};

class ServiceID : public TransportStreamID {
public:
   ServiceID(int so, int n, int t, int si) : TransportStreamID(so, n, t), sid(si) {}
   ServiceID(TransportStreamID ts, int si) : TransportStreamID(ts), sid(si) {}
   ServiceID() : sid(0) {}
   int sid;
   bool operator==(const ServiceID &other) const
      { return sid==other.sid && TransportStreamID::operator==(other); }
   bool operator!=(const ServiceID &other) const
      { return !operator==(other); }
   bool equals(const TransportStreamID &other, int si) const
      { return sid==si && TransportStreamID::operator==(other); }
   bool equals(int so, int n, int t, int si) const
      { return sid==si && TransportStreamID::equals(so, n, t); }
};

//A wrapper for TransportStreamID with Get* methods, no setters (immutable), and support for cChannel
class TransportStream {
public:
   TransportStream(int source, int onid, int tid) : data(source, onid, tid) {}
   TransportStream(cChannel *channel);
   
   TransportStreamID GetTransportStreamID() const { return data; }
   int GetSource() const { return data.source; }
   int GetNid() const { return data.onid; }
   int GetTid() const { return data.tid; }
   
   bool operator==(const TransportStream &other) const
      { return other.data==data; }
   bool operator!=(const TransportStream &other) const
      { return !operator==(other); }
   bool operator==(const TransportStreamID &other) const
      { return other==data; }
   bool operator!=(const TransportStreamID &other) const
      { return other!=data; }
   bool equals(int so, int n, int t) const
      { return data.equals(so, n, t); }
   operator TransportStreamID() { return data; }
private:
   TransportStreamID data;
};

//Two Service classes with support for subclasses of TransportStream

//For use with TransportStream. Support for cChannel.
class Service : protected TransportStream {
public:
   Service(int so, int n, int t, int sid) : TransportStream(so,n, t), sid(sid) {}
   Service(cChannel *channel);
   
   TransportStreamID GetTransportStreamID() const { return TransportStream::GetTransportStreamID(); }
   int GetSource() const { return TransportStream::GetSource(); }
   int GetNid() const { return TransportStream::GetNid(); }
   int GetTid() const { return TransportStream::GetTid(); }
   int GetSid() const { return sid; }
   ServiceID GetServiceID() const { return ServiceID(GetTransportStreamID(), sid); }
   
   bool operator==(const Service &other) const
      { return sid==other.sid && TransportStream::operator==(other); }
   bool operator!=(const Service &other) const
      { return !operator==(other); }
   bool operator==(const ServiceID &other) const
      { return other.equals(GetTransportStreamID(), sid); }
   bool operator!=(const ServiceID &other) const
      { return !operator==(other); }
   operator ServiceID() { return ServiceID(GetTransportStreamID(), sid); }
private:
   int sid;
};

//For use with a class which is a subclass of TransportStream, but
//without constrictions about its constructor
template <class T>
class ServiceAndTransportStream {
public:
   ServiceAndTransportStream(T *ts, int sid) : ts(ts), sid(sid) {}
   
   TransportStreamID GetTransportStreamID() const { return ts->GetTransportStreamID(); }
   int GetSource() const { return ts->GetSource(); }
   int GetNid() const { return ts->GetNid(); }
   int GetTid() const { return ts->GetTid(); }
   int GetSid() const { return sid; }
   ServiceID GetServiceID() const { return ServiceID(GetTransportStreamID(), sid); }
   
   bool operator==(const ServiceAndTransportStream<T> &other) const
      { return sid==other.sid && (*other.ts)==(*ts); }
   bool operator!=(const ServiceAndTransportStream<T> &other) const
      { return !operator==(other); }
   bool operator==(const ServiceID &other) const
      { return other.equals(ts->GetTransportStreamID(), sid); }
   bool operator!=(const ServiceID &other) const
      { return !operator==(other); }
   operator ServiceID() { return ServiceID(ts->GetTransportStreamID(), sid); }
protected:
   T *ts;
   int sid;
};


}

#endif

