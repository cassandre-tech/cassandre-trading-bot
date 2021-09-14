require('isomorphic-fetch');

test('Getting a trade', () => {
	
	// The result we are expecting from the GraphQL API.
	const expectedReply = {
		"accountByAccountId": {
			"accountId": "trade",
			"name": "trade"
		}
	};

	// The query of the GraphQL API server.
	return fetch('http://localhost:8080/graphql', {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify({ query: 
			`query {
				accountByAccountId(accountId:"trade"){accountId, name}
			}`
		}),
	})
	.then(res => res.json())
	.then(res => expect(res.data).toStrictEqual(expectedReply));
});
