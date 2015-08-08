# Example Usage

```bash
curl "localhost:8080/hello?x=20&y=20&dist=5" | curl -H "Content-Type: application/octet-stream" --data-binary @- mapfart.com/api/fart
```