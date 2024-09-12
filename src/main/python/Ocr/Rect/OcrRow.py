from Ocr.Rect.WordRectangle import WordRectangle, WordRelation
class OcrRow:
    def __init__(self, first_word) :
        self.words = []
        self.words.append(first_word)

    def is_in_row(self, new_word):
        if not isinstance(new_word,WordRectangle):
            return None
        
        row_marker = self.words[0]
        if(row_marker.is_in_same_line(new_word) or new_word.is_in_same_line(row_marker)):
            return WordRelation.SAME
        
        elif (row_marker.middle_point[1] < new_word.middle_point[0]):
            return WordRelation.ABOVE
        
        elif (row_marker.middle_point[1] > new_word.middle_point[0]):
            return WordRelation.BELOW

    def add_word(self, new_word):
        if not isinstance(new_word,WordRectangle):
            return
        
        place_found  = False
        for i in range(len(self.words)):
            relation = new_word.order(self.words[i])
            if relation == WordRelation.LEFT:
                self.words.insert(i, new_word)
                place_found = True
                break

        if not place_found:
            self.words.insert(0, new_word)

    def get_text(self):
        text = ""
        for word_index in range(len(self.words)):
            if word_index == 0:
                text+=self.words[word_index].text
            else:
                text+=" "+self.words[word_index].text
        return text                    
 
