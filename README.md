AutoTestAPIs - FX Trading System Test Automation Framework

Enterprise-grade testing framework for foreign exchange trading systems, supporting:

🔹 Core Testing Capabilities
- FIX 4.4 protocol implementation with session management
- WSS (WebSocket Secure) client/server for real-time market data
- ZeroMQ-based quote publishing/consumption system
- Bid/ask spread simulation with dynamic price generation
- Multi-threaded order book validation

🔹 Advanced Features
- SSL/TLS secured communication channels
- Quote latency analysis (0-5ms+ granularity)
- Duplicate quote pattern detection
- Trade confirmation workflows with execution reports
- Historical tick data replay engine
- Cross-protocol message validation (FIX <-> WebSocket)

🔹 Technical Architecture
- Core: Java 11 + Maven
- Networking: Netty 4.x, Apache MINA, ZeroMQ/JeroMQ
- Security: X.509 cert handling, FIX session auth
- Data: Binary WebSocket frames, FIX tag parsing
- Monitoring: Log4j instrumentation, execution metrics

🔹 Use Cases
- Liquidity provider quote validation
- Exchange connectivity testing
- Market data feed conformance checks
- Trade execution latency benchmarking
- Disaster recovery scenario testing


This repository contains proprietary components developed for forex trading system validation. Unauthorized use prohibited.
