def main():
    inlink_map = {}

    with open("out.link.map.txt") as file:
        for line in file:
            parts = line.split(" ")
            url = parts[0]
            outlinks = parts[1:]

            for link in outlinks:
                if link in inlink_map:
                    previous_inlinks = inlink_map[link]
                    previous_inlinks.append(url)
                    inlink_map[link] = previous_inlinks
                else:
                    inlink_map[link] = [url]

    print "Inlinks map creted with size ", len(inlink_map)

    with open('in.link.map.txt', 'w+') as inlinks_file:
        for link, inlinks in inlink_map.iteritems():
            record = link
            for inlink in inlinks:
                record += " " + inlink
            inlinks_file.write(record + "\n")

    print "inlinks file created successfully."


if __name__ == '__main__':
    main()
