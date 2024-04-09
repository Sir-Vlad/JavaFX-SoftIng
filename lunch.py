import os
import signal
import socket
from subprocess import Popen

import psutil


def find_free_port():
    """
    Cerca una porta libera da 5000 a 65535
    :rtype: int
    :return: porta libera
    """
    for port in range(5000, 65535):
        try:
            # family = utilizzare IPv4
            # type = protocollo TCP
            with socket.socket(family=socket.AF_INET, type=socket.SOCK_STREAM) as s:
                # lega qualunque interfaccia di rete del mio computer `""` a una specifica porta
                s.bind(("", port))
                return port
        except OSError:
            pass
    raise Exception("Impossibile trovare una porta libera")


def close_process_with_port(port: int):
    # uccide tutti i processi collegati alla porta
    processes = []
    # itera tutti i processi in esecuzione
    for process in psutil.process_iter():
        try:
            # tenta di ottenere le connessioni di rete del processo corrente
            connections = process.connections()
        except psutil.Error:
            continue

        # se il try ha avuto successo aggiunge alla lista solo le connessione uguali alla porta
        # passata come parametro
        for connection in connections:
            if connection.laddr.port == port:
                pip = process.pid
                processes.append(pip)

    # uccido tutti i processi che ho trovato
    for process in processes:
        os.kill(process, signal.SIGKILL)


def main() -> None:
    port: int = 0
    try:
        port = find_free_port()
    except Exception as e:
        print(e)
        exit(-1)

    backend_cmd = ["flask", "--app", "run", "run", "-p", f"{port}", "--debug"]
    frontend_cmd = ["mvn", "javafx:run", f"-Dport={port}"]

    process_backend: Popen = Popen(backend_cmd)

    process_frontend: Popen = Popen(frontend_cmd)

    # se entrambi i processi sono ancora vici continua altrimenti uccidili entrambi
    while process_frontend.poll() is None and process_backend.poll() is None:
        continue
    else:
        process_backend.kill()
        process_frontend.kill()

    close_process_with_port(port)


if __name__ == "__main__":
    main()
