require("isomorphic-fetch");

test("Getting a trade", () => {
	
	// The result we are expecting from the GraphQL API.
	const expectedReply = {
		"trade": {
			"tradeId": "60ddfbc12e113d29238c57ea"
		}
	};

	// The query of the GraphQL API server.
	return fetch("http://localhost:8080/graphql", {
		method: "POST",
		headers: { "Content-Type": "application/json", "X-API-Key": "667341fd-d4c2-4bc2-99af-0a2a697aa134" },
		body: JSON.stringify({ query: 
			`query {
				trade(uid:1){tradeId}
			}`
		}),
	})
	.then((res) => res.json())
	.then((res) => {expect(res.data).toStrictEqual(expectedReply);});
});
