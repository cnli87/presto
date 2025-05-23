/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// ArrowColumnHandle is special since it needs an implementation of
// operator<().

namespace facebook::presto::protocol::arrow_flight {
struct ArrowColumnHandle : public ColumnHandle {
  String columnName = {};
  Type columnType = {};

  ArrowColumnHandle() noexcept;

  bool operator<(const ColumnHandle& o) const override {
    return columnName < dynamic_cast<const ArrowColumnHandle&>(o).columnName;
  }
};

void to_json(json& j, const ArrowColumnHandle& p);
void from_json(const json& j, ArrowColumnHandle& p);

} // namespace facebook::presto::protocol::arrow_flight
