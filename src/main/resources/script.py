
if '__name__' == '__main__':
	setDocIds()


def setDocIds():
	docFile = "document.summary.txt"

	with open(docFile) as f:
	    content = f.readlines()

	content = [x for x in content] 
	print content