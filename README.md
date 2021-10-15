# Neo4j Natural Sorting
This plugin adds two functions to Neo4j which solves the problem of natural sorting for strings

## Sorting example

Dataset:
```
=A100
=A10
=A2
=A30
=A1
=A4
```

Standard sorting implemented in Neo4j and APOC:
```
=A1
=A10
=A100
=A2
=A30
=A4
```

Natural sorting
```
=A1
=A2
=A4
=A10
=A30
=A100
```

## Usage example

There are two methods implemented in the plugin - one for sorting by specific props of node and one for sorting list of strings.

### Sorting list of strings

```
RETURN sort.natural_list(['=A2', '=A10', '=A1']) AS result
```

### Sorting list of nodes by specific property

```
CREATE (Node{name: 'n1', rfd: '=A2'})
CREATE (Node{name: 'n2', rfd: '=A10'})
CREATE (Node{name: 'n3', rfd: '=A1'})

MATCH (object:Node)
RETURN sort.natural_obj(collect(object), 'rfd') AS result"
```

Also, there is an ability to reverse sorting - `^` char before property name means that sorting should be reversed:

```
CREATE (Node{name: 'n1', rfd: '=A2'})
CREATE (Node{name: 'n2', rfd: '=A10'})
CREATE (Node{name: 'n3', rfd: '=A1'})

MATCH (object:Node)
RETURN sort.natural_obj(collect(object), '^rfd') AS result"
```

## Java Natural Order Comparator

NaturalOrderComparator.java -- Perform 'natural order' comparisons of strings in Java.
 Copyright (C) 2003 by Pierre-Luc Paour <natorder@paour.com>

 Based on the C version by Martin Pool, of which this is more or less a straight conversion.
 Copyright (C) 2000 by Martin Pool <mbp@humbug.org.au>

 This software is provided 'as-is', without any express or implied
 warranty.  In no event will the authors be held liable for any damages
 arising from the use of this software.

 Permission is granted to anyone to use this software for any purpose,
 including commercial applications, and to alter it and redistribute it
 freely, subject to the following restrictions:

 1. The origin of this software must not be misrepresented; you must not
 claim that you wrote the original software. If you use this software
 in a product, an acknowledgment in the product documentation would be
 appreciated but is not required.
 2. Altered source versions must be plainly marked as such, and must not be
 misrepresented as being the original software.
 3. This notice may not be removed or altered from any source distribution.

## Java Natural Order Node Comparator

NaturalOrderNodeComparator.java -- Perform 'natural order' comparisons of Neo4j nodes in Java.
 Copyright (C) 2003 by Pierre-Luc Paour <natorder@paour.com> with small changes from [Wojciech Maciejak](https://github.com/wmaciejak)

 Based on the C version by Martin Pool, of which this is more or less a straight conversion.
 Copyright (C) 2000 by Martin Pool <mbp@humbug.org.au>

 This software is provided 'as-is', without any express or implied
 warranty.  In no event will the authors be held liable for any damages
 arising from the use of this software.

 Permission is granted to anyone to use this software for any purpose,
 including commercial applications, and to alter it and redistribute it
 freely, subject to the following restrictions:

 1. The origin of this software must not be misrepresented; you must not
 claim that you wrote the original software. If you use this software
 in a product, an acknowledgment in the product documentation would be
 appreciated but is not required.
 2. Altered source versions must be plainly marked as such, and must not be
 misrepresented as being the original software.
 3. This notice may not be removed or altered from any source distribution.

## Contributing

1. Fork it
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create new Pull Request


