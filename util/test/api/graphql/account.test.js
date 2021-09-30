require("isomorphic-fetch");

test("Getting an account", () => {
	
	// The result we are expecting from the GraphQL API.
	const expectedReply = {
		"accountByAccountId": {
			"accountId": "trade",
			"name": "trade"
		}
	};

	// The query of the GraphQL API server.
	return fetch("http://localhost:8080/graphql", {
		method: "POST",
		headers: { "Content-Type": "application/json", "X-API-Key": "667341fd-d4c2-4bc2-99af-0a2a697aa134" },
		body: JSON.stringify({ query: 
			`query {
				accountByAccountId(accountId:"trade"){accountId, name}
			}`
		}),
	})
	.then((res) => res.json())
	.then((res) => {expect(res.data).toStrictEqual(expectedReply);});
});
