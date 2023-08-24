"""Test the API endpoints of the containerized application."""

import requests
import unittest


class APITest(unittest.TestCase):
    """Test the API endpoints of the containerized application."""

    def setUp(self):
        """setUp method for controlling the test environment."""
        self.base_url = "localhost:3000"
        # self.base_url = "http://sysarch.digital.isc.fraunhofer.de:3000"
        self.graph_name = "api-test"
        self.prep_data_gname = "api-test-data"
        self.print_test_info = False

    def test_graph_create(self):
        """Test the graph_create endpoint."""
        endpoint = "/rpc/graph_create"
        url = f"{self.base_url}{endpoint}"
        headers = {"Content-Type": "application/json"}
        payload = {"graph_name": f"{self.graph_name}"}
        response = requests.post(url, headers=headers, json=payload)
        if self.print_test_info:
            print("\n" + url)
            print(response.json())
        self.assertIsNotNone(response.json())
        self.assertEqual(response.status_code, 200)

    def test_graph_delete(self):
        """Test the graph_delete endpoint."""
        endpoint = "/rpc/graph_delete"
        url = f"{self.base_url}{endpoint}"
        headers = {"Content-Type": "application/json"}
        payload = {"graph_name": f"{self.graph_name}"}
        response = requests.post(url, headers=headers, json=payload)
        if self.print_test_info:
            print("\n" + url)
            print(response.json())
        self.assertIsNotNone(response.json())
        self.assertEqual(response.status_code, 200)

    def test_rapsql_query(self):
        """Test the rapsql_query endpoint."""
        endpoint = "/rpc/rapsql_query"
        url = f"{self.base_url}{endpoint}"
        headers = {"Content-Type": "application/json"}
        payload = {
            "querystring": "SELECT * FROM ag_catalog.cypher('api-test-data', $$ MATCH (x)-[{uri:'http://example.org/ns#price'}]->(price),(x)-[{uri:'http://purl.org/dc/elements/1.1/title'}]->(title) WHERE price.value < 30.5 RETURN title.stringrep, price.stringrep $$) AS (title ag_catalog.agtype, price ag_catalog.agtype)"  # noqa: E501
        }
        response = requests.post(url, headers=headers, json=payload)
        if self.print_test_info:
            print("\n" + url)
            print(response.json())
        self.assertIsNotNone(response.json())
        self.assertEqual(response.status_code, 200)

    def test_rapsql_s2c(self):
        """Test the rapsql_s2c endpoint."""
        endpoint = "/rpc/rapsql_s2c"
        url = f"{self.base_url}{endpoint}"
        headers = {"Content-Type": "application/json"}
        payload = {
            "graph_name": f"{self.prep_data_gname}",
            "sparql_query": "PREFIX dc: <http://purl.org/dc/elements/1.1/> PREFIX ns: <http://example.org/ns#> SELECT ?title ?price WHERE { ?x ns:price ?price . FILTER (?price < 30.5) ?x dc:title ?title . }",  # noqa: E501
        }
        response = requests.post(url, headers=headers, json=payload)
        if self.print_test_info:
            print("\n" + url)
            print(response.json())
        self.assertIsNotNone(response.json())
        self.assertEqual(response.status_code, 200)

    def test_rapsql_r2c(self):
        """Test the rapsql_s2c endpoint."""
        endpoint = "/rpc/rapsql_r2c"
        url = f"{self.base_url}{endpoint}"
        headers = {"Content-Type": "application/json"}
        payload = {
            "graph_name": f"{self.prep_data_gname}",
            "lang_str": "TTL",
            "rdf_str": "@prefix dc: <http://purl.org/dc/elements/1.1/> . @prefix : <http://example.org/book/> . @prefix ns: <http://example.org/ns#> . :book1 dc:title 'SPARQL Tutorial' . :book1 ns:price 42 . :book2 dc:title 'The Semantic Web' . :book2 ns:price 23 .",  # noqa: E501
        }
        response = requests.post(url, headers=headers, json=payload)
        if self.print_test_info:
            print("\n" + url)
            print(response.json())
        self.assertIsNotNone(response.json())
        self.assertEqual(response.status_code, 200)

    def test_rapsql_cypher(self):
        """Test the rapsql_cypher endpoint."""
        endpoint = "/rpc/rapsql_cypher"
        url = f"{self.base_url}{endpoint}"
        headers = {"Content-Type": "application/json"}
        payload = {
            "cypher_query": "SELECT * FROM ag_catalog.cypher('api-test-data', $$ MATCH (x)-[{uri:'http://example.org/ns#price'}]->(price),(x)-[{uri:'http://purl.org/dc/elements/1.1/title'}]->(title) WHERE price.value < 30.5 RETURN title.stringrep, price.stringrep $$) AS (title ag_catalog.agtype, price ag_catalog.agtype)"  # noqa: E501
        }
        response = requests.post(url, headers=headers, json=payload)
        if self.print_test_info:
            print("\n" + url)
            print(response.json())
        self.assertIsNotNone(response.json())
        self.assertEqual(response.status_code, 200)

    def test_rapsql_sparql(self):
        """Test the rapsql_sparql endpoint."""
        endpoint = "/rpc/rapsql_sparql"
        url = f"{self.base_url}{endpoint}"
        headers = {"Content-Type": "application/json"}
        payload = {
            "graph_name": f"{self.prep_data_gname}",
            "sparql_query": "PREFIX dc: <http://purl.org/dc/elements/1.1/> PREFIX ns: <http://example.org/ns#> SELECT ?title ?price WHERE{ ?x ns:price ?price . FILTER (?price < 30.5) ?x dc:title ?title . }",  # noqa: E501
        }
        response = requests.post(url, headers=headers, json=payload)
        if self.print_test_info:
            print("\n" + url)
            print(response.json())
        self.assertIsNotNone(response.json())
        self.assertEqual(response.status_code, 200)


if __name__ == "__main__":
    unittest.main()
