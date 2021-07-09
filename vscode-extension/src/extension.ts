'use strict';

import * as fs from 'fs';
import * as path from 'path';
import * as events from 'events';
import * as vscode from 'vscode';
import * as rx from 'rx-lite';

import { /*workspace,*/ ExtensionContext } from 'vscode';
//import Module = require('module');
//import { LanguageClient, LanguageClientOptions, ServerOptions } from 'vscode-languageclient';

var mugeneDirPath = "../../../mugene/build/publications/npm/js";
var mugeneJSPath = mugeneDirPath + "/mugene-ng-mugene.js";
if (fs.existsSync(module.path + "/" + mugeneJSPath)) {
	var mugene = require(mugeneJSPath); // path under dev. environment.
	mugene.dev.atsushieno.mugene.NodeModuleResourceStreamResolver.Companion.instance.basePath = module.path + "/" + mugeneDirPath;
} else {
	var mugene = require("@dev.atsushieno/mugene/mugene-ng-mugene.js");
	mugene.dev.atsushieno.mugene.NodeModuleResourceStreamResolver.Companion.instance.basePath = __dirname + "/../../node_modules/@dev.atsushieno/mugene";
}

const mugene_scheme = "mugene";

var diagnostics : vscode.DiagnosticCollection;

class MugeneTextDocumentContentProvider implements vscode.TextDocumentContentProvider, vscode.Disposable {
	private _onDidChange = new vscode.EventEmitter<vscode.Uri> ();
	private _emitter = new events.EventEmitter ();

	private _subscription = rx.Observable.fromEvent<vscode.TextDocumentChangeEvent> (this._emitter, "data")
		.sample (rx.Observable.interval (1000))
		.subscribe (event => {
			if (event.document === vscode.window.activeTextEditor.document) {
				this.update (getSpecialSchemeUri (event.document.uri));
			}
		});

	public dispose () {
		this._subscription.dispose ();
	}

	public provideTextDocumentContent (uri: vscode.Uri): string | Thenable<string> {
		return vscode.workspace.openTextDocument (vscode.Uri.parse (uri.query)).then (doc => {
			return this.convert (doc);
		});
	}

	get onDidChange(): vscode.Event<vscode.Uri> {
		return this._onDidChange.event;
	}

	public update (uri: vscode.Uri) {
		this._onDidChange.fire (uri);
	}

	private convert (document: vscode.TextDocument): string | Promise<string> {
		return new Promise ((resolve, rejected) => {
			processDocument (document).then (
                buf => resolve (buf),
				reason => rejected (reason)
			);
		});
	}
}

function showPreview (uri: vscode.Uri) {
	if (!(uri instanceof vscode.Uri)) {
		if (vscode.window.activeTextEditor) {
			uri = vscode.window.activeTextEditor.document.uri;
		}
	}
	return vscode.commands.executeCommand ('vscode.previewHtml', getSpecialSchemeUri (uri), vscode.ViewColumn.Two);
}

function getSpecialSchemeUri (uri: any): vscode.Uri {
	return uri.with({
		scheme: mugene_scheme,
		path: uri.path,
		query: uri.toString ()
	});
}

function compileMugene (uri: vscode.Uri, _ : ExtensionContext) {
	compileMugeneCommon(false, uri, _);
}

function compileMugene2 (uri: vscode.Uri, _ : ExtensionContext) {
	compileMugeneCommon(true, uri, _);
}

function compileMugeneCommon (isMidi2: Boolean, uri: vscode.Uri, _ : ExtensionContext) {
	if (!(uri instanceof vscode.Uri)) {
		if (vscode.window.activeTextEditor) {
			uri = vscode.window.activeTextEditor.document.uri;
		}
	}

	var input = new mugene.dev.atsushieno.mugene.MmlInputSource(uri.fsPath,
		vscode.window.activeTextEditor.document.getText());

	// clean up existing diagnostic reports
	if (diagnostics != null)
		diagnostics.dispose();

	// Collect error reports
	var reports = new Array<vscode.Diagnostic> ();
	var compiler = mugene.dev.atsushieno.mugene.MmlCompiler.Companion.create();
	compiler.continueOnError = true;
	compiler.report = function(verbosity: any, location: any, message: any) {
		if (location.file == uri.fsPath)
			reports.push(createDiagnostic(verbosity, location, message));
		else
			console.log(location.file + ": " + message);
	};

	if (isMidi2) {
		var music = compiler.compile2(true, false, [input]);
		if (music != null) {
			var bytes = mugene.dev.atsushieno.mugene.midi2MusicToByteArray(music);
	
			var pathExt = path.extname(uri.fsPath);
			var midiFilePath = uri.fsPath.substring(0, uri.fsPath.length - pathExt.length) + ".umpx";

			fs.writeFile(midiFilePath, Buffer.from(bytes), () => {});
		}
	} else {
		var music = compiler.compile(false, [input]);
		if (music != null) {
			var bytes = mugene.dev.atsushieno.mugene.midiMusicToByteArray(music);
	
			var pathExt = path.extname(uri.fsPath);
			var midiFilePath = uri.fsPath.substring(0, uri.fsPath.length - pathExt.length) + ".mid";
	
			fs.writeFile(midiFilePath, Buffer.from(bytes), () => {});
		}
	}
	if (music != null)	// either MIDI1 or MIDI2
		vscode.window.showInformationMessage("mugene successfully finished");
	else
		vscode.window.showInformationMessage("compilation failed.");

	// Show compiler reports as vscode diagnostics
	diagnostics = vscode.languages.createDiagnosticCollection("mugene");
	diagnostics.set (uri, reports);
}

function createDiagnostic(verbosity: any, location: any, message: any): vscode.Diagnostic {
	var line = location.lineNumber - 1;
	var col = location.linePosition;
	var type = vscode.DiagnosticSeverity.Information;
	if (verbosity == mugene.dev.atsushieno.mugene.MmlDiagnosticVerbosity.Error)
		type = vscode.DiagnosticSeverity.Error;
	else if (verbosity == mugene.dev.atsushieno.mugene.MmlDiagnosticVerbosity.Warning)
		type = vscode.DiagnosticSeverity.Warning;
	return new vscode.Diagnostic(new vscode.Range (line, col, line, col), message.toString(), type);
}

function processDocument (_: vscode.TextDocument) : Promise<string> {
    // process vexflow
    return Promise.resolve ("done");
}


export function activate(context: ExtensionContext) {
	activateCompiler(context)
	activatePreview(context)
	// FIXME: enable it once issue is fixed
	//activateLSP(context)
}

function activateCompiler(context: ExtensionContext) {
	// Compile command
	let compileCommand = vscode.commands.registerCommand ("mugene.compile", uri => compileMugene (uri, context));
	context.subscriptions.push(compileCommand);
	let compile2Command = vscode.commands.registerCommand ("mugene.compile2", uri => compileMugene2 (uri, context));
	context.subscriptions.push(compile2Command);
}

function activatePreview(context: ExtensionContext) {

	let previewDocProvider = new MugeneTextDocumentContentProvider ();
	vscode.workspace.onDidChangeTextDocument ((event: vscode.TextDocumentChangeEvent) => {
		if (event.document === vscode.window.activeTextEditor.document) {
			previewDocProvider.update (getSpecialSchemeUri (event.document.uri));
		}
	});
	let registration = vscode.workspace.registerTextDocumentContentProvider (mugene_scheme, previewDocProvider);
	let cmd = vscode.commands.registerCommand ("mugene.showPreview", uri => showPreview (uri), vscode.ViewColumn.Two);
	context.subscriptions.push (cmd, registration, previewDocProvider);

}

// FIXME: the LSP server somehow causes 100% CPU usage on mono. Disable it until the issue gets fixed.
/*
function activateLSP(context: ExtensionContext) {

	// The server is implemented in C#
	let serverCommand = context.asAbsolutePath(path.join('out', 'server', 'mugene.languageserver.tool.exe'));
	let commandOptions = { stdio: 'pipe' };
	
	// If the extension is launched in debug mode then the debug server options are used
	// Otherwise the run options are used
	let serverOptions: ServerOptions =
		(os.platform() === 'win32') ? {
			run : { command: serverCommand, options: commandOptions },
			debug: { command: serverCommand, options: commandOptions }
		} : {
			run : { command: 'mono', args: ["--debug", serverCommand], options: commandOptions },
			debug: { command: 'mono', args: ["--debug", serverCommand], options: commandOptions }
		}
	
	// Options to control the language client
	let clientOptions: LanguageClientOptions = {
		// Register the server for plain text documents
		documentSelector: [{scheme: 'file', language: 'mugene'}],
		synchronize: {
			// Synchronize the setting section 'languageServerExample' to the server
			configurationSection: 'mugene',
			// Notify the server about file changes to '.clientrc files contain in the workspace
			fileEvents: workspace.createFileSystemWatcher('** /.clientrc')
		}
	}
	
	// Create the language client and start the client.
	let lsp = new LanguageClient('mugene', 'mugene Language Server', serverOptions, clientOptions).start();
	
	// Push the disposable to the context's subscriptions so that the 
	// client can be deactivated on extension deactivation
	context.subscriptions.push(lsp);
}
*/
