module api.form {

    export class ValidationRecording {

        private breaksMinimumOccurrencesArray: ValidationRecordingPath[] = [];

        private breaksMaximumOccurrencesArray: ValidationRecordingPath[] = [];

        breaksMinimumOccurrences(path: ValidationRecordingPath) {
            if (!this.exists(path, this.breaksMinimumOccurrencesArray)) {
                this.breaksMinimumOccurrencesArray.push(path);
            }
        }

        breaksMaximumOccurrences(path: ValidationRecordingPath) {
            if (!this.exists(path, this.breaksMaximumOccurrencesArray)) {
                this.breaksMaximumOccurrencesArray.push(path);
            }
        }

        isValid(): boolean {
            return this.breaksMinimumOccurrencesArray.length == 0 && this.breaksMaximumOccurrencesArray.length == 0;
        }

        flatten(recording: ValidationRecording) {

            recording.breaksMinimumOccurrencesArray.forEach((path: ValidationRecordingPath)=> {
                this.breaksMinimumOccurrences(path);
            });

            recording.breaksMaximumOccurrencesArray.forEach((path: ValidationRecordingPath)=> {
                this.breaksMaximumOccurrences(path);
            });
        }

        removeByPath(path: ValidationRecordingPath) {

            this.removeUnreachedMinimumOccurrencesByPath(path);
            this.removeBreachedMaximumOccurrencesByPath(path);
        }

        removeUnreachedMinimumOccurrencesByPath(path: ValidationRecordingPath) {

            for (var i = this.breaksMinimumOccurrencesArray.length - 1; i >= 0; i--) {
                if (this.breaksMinimumOccurrencesArray[i].toString().indexOf(path.toString()) == 0) {
                    this.breaksMinimumOccurrencesArray.splice(i, 1);
                }
            }
        }

        removeBreachedMaximumOccurrencesByPath(path: ValidationRecordingPath) {

            for (var i = this.breaksMaximumOccurrencesArray.length - 1; i >= 0; i--) {
                if (this.breaksMaximumOccurrencesArray[i].toString().indexOf(path.toString()) == 0) {
                    this.breaksMaximumOccurrencesArray.splice(i, 1);
                }
            }
        }

        equals(other: ValidationRecording): boolean {

            if (this.breaksMinimumOccurrencesArray.length != other.breaksMinimumOccurrencesArray.length) {
                return false;
            }
            else if (this.breaksMaximumOccurrencesArray.length != other.breaksMaximumOccurrencesArray.length) {
                return false;
            }

            for (var i = 0; i < this.breaksMinimumOccurrencesArray.length; i++) {
                if (this.breaksMinimumOccurrencesArray[i].toString() != other.breaksMinimumOccurrencesArray[i].toString()) {
                    return false;
                }
            }

            for (var i = 0; i < this.breaksMaximumOccurrencesArray.length; i++) {
                if (this.breaksMaximumOccurrencesArray[i].toString() != other.breaksMaximumOccurrencesArray[i].toString()) {
                    return false;
                }
            }

            return true;
        }

        validityChanged(previous: api.form.ValidationRecording): boolean {
            return previous == undefined || previous == null || !previous.equals(this);
        }

        toString(): string {
            if (this.isValid()) {
                return "";
            } else {
                var out = "<ul>";
                this.breaksMinimumOccurrencesArray.forEach((path: ValidationRecordingPath, index: number) => {
                    out += "<li>" + path.toString() + " - Minimum " + path.getMin() + " occurence(s) must exist</li>";
                });
                this.breaksMaximumOccurrencesArray.forEach((path: ValidationRecordingPath, index: number) => {
                    out += "<li>" + path.toString() + " - Maximum " + path.getMax() + " occurence(s) must exist</li>";
                });
                return out += "</ul>";
            }
        }

        private exists(path: ValidationRecordingPath, array: ValidationRecordingPath[]): boolean {
            for (var i = 0; i < array.length; i++) {
                if (array[i].toString() == path.toString()) {
                    return true;
                }
            }
            return false;
        }
    }
}
