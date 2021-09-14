require('isomorphic-fetch');

test('Getting a position', () => {
	
	// The result we are expecting from the GraphQL API.
	const expectedReply = {
		"position": {
			"positionId": 2
		}
	};

	// The query of the GraphQL API server.
	return fetch('http://localhost:8080/graphql', {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify({ query: 
			`query {
				position(id:3){positionId}
			}`
		}),
	})
	.then(res => res.json())
	.then(res => expect(res.data).toStrictEqual(expectedReply));
});
