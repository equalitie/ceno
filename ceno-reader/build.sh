#! /usr/bin/env sh

echo "Formatting go files."
for file in `ls *.go`; do
  go fmt $file;
  echo $file;
done

echo ""
echo "Installing dependencies."
echo "go-pkg-xmlx"
go get github.com/jteeuwen/go-pkg-xmlx
echo "go-pkg-rss"
go get github.com/jteeuwen/go-pkg-rss
echo "goi18n"
go get -u github.com/nicksnyder/go-i18n/goi18n
echo "go-sqlite3"
go get github.com/mattn/go-sqlite3

echo ""
#echo "Merging localizaton files."
#cd translations
#$GOPATH/bin/goi18n *.json
#cd ..

go build reader.go charsetreaders.go config.go persistence.go && echo "Compiled reader successfully."