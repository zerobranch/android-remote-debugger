/*
 * Copyright 2020 Arman Sargsyan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zerobranch.androidremotedebugger.api.database;

interface DatabaseHtmlKey {
    String GET_DATABASES = "getDatabases";
    String GET_TABLES = "getTables";
    String GET_TABLE = "getTable";
    String GET_BY_QUERY = "getByQuery";
    String UPDATE_TABLE = "updateTable";
    String DELETE_TABLE_ITEMS = "deleteTableItems";
    String DROP_TABLE = "dropTable";
    String DROP_DATABASE = "dropDatabase";
    String SEARCH = "search";
}
