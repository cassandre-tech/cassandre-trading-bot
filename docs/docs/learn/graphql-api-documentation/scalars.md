# Scalars

### About scalars

[Scalars](https://graphql.github.io/graphql-spec/June2018/#sec-Scalars) are primitive values: `Int`, `Float`, `String`, `Boolean`, or `ID`.

When calling the GraphQL API, you must specify nested subfields until you return only scalars.

### BigDecimal



---

### Boolean

<p>The <code>Boolean</code> scalar type represents <code>true</code> or <code>false</code>.</p>

---

### DateTime



---

### Float

<p>The <code>Float</code> scalar type represents signed double-precision fractional values as specified by <a href="http://en.wikipedia.org/wiki/IEEE_floating_point">IEEE 754</a>.</p>

---

### ID

<p>The <code>ID</code> scalar type represents a unique identifier, often used to refetch an object or as key for a cache. The ID type appears in a JSON response as a String; however, it is not intended to be human-readable. When expected as an input type, any string (such as <code>&quot;4&quot;</code>) or integer (such as <code>4</code>) input value will be accepted as an ID.</p>

---

### Int

<p>The <code>Int</code> scalar type represents non-fractional signed whole numeric values. Int can represent values between -(2^31) and 2^31 - 1.</p>

---

### String

<p>The <code>String</code> scalar type represents textual data, represented as UTF-8 character sequences. The String type is most often used by GraphQL to represent free-form human-readable text.</p>

---