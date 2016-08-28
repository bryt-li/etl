.PHONY: default
default:
	@echo "make png	generate png image from omnigraffle files *.graffle"

GRAFFLES := $(wildcard *.graffle)
PNGS := $(GRAFFLES:.graffle=.png)

.PHONY: png
png: $(PNGS)

%.png:%.graffle
	omnigraffle-export -f png -c "Canvas 1" $< $@
