require('isomorphic-fetch');

test('Getting configuration', () => {
	
	// The result we are expecting from the GraphQL API.
	const expectedReply = {
		"configuration": {
			"apiVersion": "1.0"
		}
	};

	// The query of the GraphQL API server.
	return fetch('http://localhost:8080/graphql', {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify({ query: 
			`query {
				configuration{ apiVersion }
			}`
		}),
	})
	.then(res => res.json())
	.then(res => expect(res.data).toStrictEqual(expectedReply));
});
