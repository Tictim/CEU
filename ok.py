import plotly.express as px
import csv
from pathlib import Path

while True:
    print("Copy-paste CSV file address to visualize it. Type 'exit' to exit.")
    while True:
        fuck = input(" : ")
        if len(fuck):
            break

    fuckLower = fuck.lower()

    if fuckLower == "exit":
        exit(0)
    elif fuckLower.endswith(".csv"):
        try:
            with open(fuck) as file:
                rows = [r for r in csv.DictReader(file)]
                fig = px.line(x=[int(r['time']) for r in rows], y=[int(r['value']) for r in rows], title=Path(fuck).stem)
                fig.show()
        except FileNotFoundError:
            print("Cannot locate file at '%s'" % fuck)
        except OSError:
            print("Cannot access file at '%s'" % fuck)
    else:
        print("Not a csv file")
