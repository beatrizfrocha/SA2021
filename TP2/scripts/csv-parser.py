import csv

with open('data/incidents.csv', 'r') as file:
    reader = list(csv.reader(file))
    reader = reader[1:]

datas = []
for i in reader:
    if(i[6] != '' and i[5][11:13] != i[6][11:13]):
        a = int(i[5][11:13])
        b = int(i[6][11:13])
        horas = [i for i in range(a,b+1)]
        for j in horas:
            datas.append(j)
    else:
        datas.append(int(i[5][11:13]))

for i in datas:
    print(i)

f = open("data/incidents_per_hour.csv", "w")

writer = csv.writer(f)
writer.writerow(["Hora"])
for i in datas:
    writer.writerow([i])

f.close()
