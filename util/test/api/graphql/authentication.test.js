require("isomorphic-fetch");

test("Accessing API with wrong API key", () => {

	// The query of the GraphQL API server.
	return fetch("http://localhost:8080/graphql", {
		method: "POST",
		headers: { "Content-Type": "application/json", "X-API-Key": "WRONG-API-KEY" },
		body: JSON.stringify({ query:
				`query {
				strategy(uid:1){ strategyId name }
			}`
		}),
	})
		.then((res) => res)
		.then((res) => {expect(res.status).toStrictEqual(403);});
});