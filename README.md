# Certificate Check

A simple OCI function that checks for certificate expiry

## Deploying 

```
fn deploy --app myapp
```

## Example
```
echo '{\
"url": "https://apigateway.us-ashburn-1.oci.oraclecloud.com", \
"cn": "identity.us-ashburn-1.oraclecloud.com"\
}' | fn invoke serverless certificate-check

➜  certificate-check git:(master) echo '{"url": "https://github.com", "cn": "github.com"}' | fn invoke serverless certificate-check
Days remaining: 365
```
