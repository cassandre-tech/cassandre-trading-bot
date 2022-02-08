require("isomorphic-fetch");

test("Getting a position", () => {
	
	// The result we are expecting from the GraphQL API.
	const expectedReply = {
		"position": {
			"positionId": 2
		}
	};

	// The query of the GraphQL API server.
	return fetch("http://localhost:8080/graphql", {
		method: "POST",
		headers: { "Content-Type": "application/json", "X-API-Key": "667341fd-d4c2-4bc2-99af-0a2a697aa134" },
		body: JSON.stringify({ query: 
			`query {
				position(uid:3){positionId}
			}`
		}),
	})
	.then((res) => res.json())
	.then((res) => {expect(res.data).toStrictEqual(expectedReply);});
});
