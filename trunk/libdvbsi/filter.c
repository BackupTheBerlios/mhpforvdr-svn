/***************************************************************************
 *       Copyright (c) 2003 by Marcel Wiesweg                              *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

#include "filter.h"
#include "database.h"

namespace DvbSi {

Filter::Filter(Database::Ptr db) : database(db), attached(false)
{
}

Filter::~Filter() {
   //detach here so that database knows
   Detach();
}

void Filter::Attach() {
   //database checks for attached, no need here
   database->Attach(this); 
}

void Filter::Detach() {
   //database checks for attached, no need here
   database->Detach(this); 
}

DatabasePtr Filter::getDatabase() {
   return database;
}

/*
RequestFilter::RequestFilter(DatabasePtr db, Request *r)
  : Filter(db), request(r)
{
}
*/

}
