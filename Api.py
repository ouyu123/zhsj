import json
from http.server import BaseHTTPRequestHandler, HTTPServer
from urllib.parse import parse_qs  # 保留这个导入，但在这个例子里不会使用它
import  pridect

class SimpleServer(BaseHTTPRequestHandler):
    def do_POST(self):
        # 检查请求路径是否为/api
        if self.path != '/api':
            self.send_error(404, "Path not found")
            return

            # 设置响应头
        self.send_response(200)
        self.send_header("Content-type", "application/json")
        self.end_headers()

        # 读取请求体
        content_length = int(self.headers['Content-Length'])
        post_data = self.rfile.read(content_length).decode('utf-8')
        print(post_data)
        try:
            data = json.loads(post_data)  # 使用 json.loads 解析 JSON 数据
        except json.JSONDecodeError as e:
            self.send_error(400, "Invalid JSON data")
            return

        print(data)

        path = data['path']

        label = pridect.main(path)

        response ={'label': label}

            # 返回响应
        self.wfile.write(json.dumps(response).encode('utf-8'))

if __name__ == "__main__":
    # 启动服务器
    server_address = ("", 8000)  # 使用空字符串表示本地地址
    httpd = HTTPServer(server_address, SimpleServer)
    print("Server running on port 8000...")
    httpd.serve_forever()
