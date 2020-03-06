 * http://qiita.com/disc99/items/31fa7abb724f63602dc9


|   | # | 機能 	| Hamcrest 	| AssertJ |
|---|---| ----- | --------- | ------- |
| V | 1 |チェック対象を指定する | assertThat |assertThat|
| V | 2 |Trueであることをチェックする 	|is 	|isTrue|
| V | 3 |Falseであることをチェックする 	|is 	|isFalse|
| V | 4 |同じ値であることをチェックする 	|is 	|isEqualTo|
| V | 5 |同じ値でないことをチェックする 	|is + not 	|isNotEqulTo|
| V | 6 |nullであることをチェックする 	|is + nullValue 	|isNull|
| V | 7 |nullでないことをチェックする 	|is + notNullValue 	|isNotNull|
| V | 8 |compareTo() で比較する 	|is + comparesEqualTo 	|isEqualByComparingTo|
| V | 9 |同じインスタンスであることをチェックする 	|is + sameInstance 	|isSameAs|
| V | 10|指定したクラスのインスタンス型であることをチェックする 	|is + instanceOf 	|isInstanceOf|
| V | 11|toString() で比較する 	|is + hasToString 	|hasToString|
| V | 12|指定した文字で始まることをチェックする 	|is + startsWith 	|startsWith|
| V | 13|指定した文字で終わることをチェックする 	|is + endsWith 	|endsWith|
| V | 14|指定した文字が含まれることをチェックする 	|is + containsString 	|contains|
| V | 15|大文字・小文字を区別せずに比較する 	|is + equalToIgnoringCase 	|isEqualToIgnoringCase|
| V | 16|大文字・小文字・ブランク文字を無視して比較する 	|is + equalToIgnoringWhiteSpace 	|isEqualToIgnoringWhitespace|
| V | 17|空文字であることをチェックする 	|isEmptyString 	|isEmpty|
| V | 18|空文字または null であることをチェックする 	|isEmptyOrNullString 	|isNullOrEmpty|
| V | 19|正規表現を使ってチェックする 	|- 	|matches|
| V | 20|数値であることをチェックする 	|- 	|containsOnlyDigits|
| V | 21|行数をチェックする 	|- 	|hasLineCount|
|   | 22|指定した順序で文字列が現れることをチェックする 	|is + stringContainsInOrder 	|containsSequence|
|   | 23|指定した範囲の値に近いかチェックする 	|is + closeTo 	|isCloseTo|
|   |23a|指定した範囲の値であることをチェックする 	|?|isBetweebn|
| V | 24|指定した値より大きいことをチェックする 	|is + greaterThan 	|isGreaterThan|
| V | 25|指定した値以上であることをチェックする 	|is + greaterThanOrEqualTo 	|isGreaterThanOrEqualTo|
| V | 26|指定した値より小さいことをチェックする 	|is + lessThan 	|isLessThan|
| V | 27|指定した値以下であることをチェックする 	|is + lessThanOrEqualTo 	|isLessThanOrEqualTo|
| V | 28|全ての要素が等しいことをチェックする 	|is + contains ?|contains?|
| V | 29|指定した要素を持つことをチェックする 	|hasItem 	|containsOnly|
| V | 30|指定した要素を全て含むことをチェックする 	|hasItems 	|        |
| V |   |(containsExactly)                     |           |containsOnly|
| V | 31|指定したサイズであることをチェックする 	|hasSize 	|hasSize|
| V | 32|空であることをチェックする 	|is + empty 	|isEmpty|
| V | 33|空の Iterator であることをチェックする 	|is + emptyIterable 	|isEmpty|
|   | 34|指定したエントリを持つことをチェックする 	|hasEntry 	|containsEntry|
|   | 35|指定したキーを持つことをチェックする 	|hasKey 	|containsKey|
|   | 36|指定した値を持つことをチェックする 	|hasValue 	|containsValue|
| - | 27|指定したプロパティを持つことをチェックする 	|hasProperty 	|filteredOn|
| - | 38|全てのプロパティが等しいことをチェックする 	|is + samePropertyValuesAs 	|isEqualToComparingFieldByField|
| ? | 39|指定した要素と配列の要素が全て等しいことをチェックする 	|is + arrayContaining 	|containsExactly|
| V | 40|順序を無視して、指定した要素が全て配列に含まれることをチェックする 	|is + arrayContainingInAnyOrder 	|contains|
| V | 41|配列のサイズをチェックする 	|is + arrayWithSize 	|hasSize|
| V | 42|配列が空であることをチェックする 	|is + emptyArray 	|isEmpty|
| V | 43|配列の中に指定した値が存在することをチェックする 	|hasItemInArray 	|contains|
| N | 44|テストに失敗したときのメッセージを上書きする 	|describedAs 	|as|
|   |   |                                       |               |softAssertions